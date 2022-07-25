package com.lulu.springmvc.context.impl;

import com.lulu.springmvc.annotation.Autowired;
import com.lulu.springmvc.annotation.ComponentScan;
import com.lulu.springmvc.annotation.Controller;
import com.lulu.springmvc.annotation.Service;
import com.lulu.springmvc.bean.BeanDefinition;
import com.lulu.springmvc.context.ApplicationContext;
import com.lulu.springmvc.handler.HandlerInterceptor;
import com.lulu.springmvc.util.FileUtil;
import com.lulu.springmvc.util.XmlPaser;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/0:09
 * @Description: springMvc continer(这里其实相当于beanFactory) 简单实现了循环依赖解决方案
 */
public class WebMvcAppliactionContext implements ApplicationContext {

   private static String contextConfigLocation;

   private final  Map<String,BeanDefinition> beanDefinitionMap = new HashMap();
   //预注册类名
   private final List<String> beanRegisterList = new ArrayList<>();
   //一级缓存
   private final Map<String, Object> beanMap= new ConcurrentHashMap<>();
   //二级缓存
   private final Map<String, Object> earlybeanMap = new HashMap<>();
   //正在创建bean
   private final Set<String> readyToCreateBean = new HashSet<>();
   //保存完成初始化的bean，防止直接暴露一级缓存（可能存在修改行为）
   private final List<String> initializeCompleteBean = new ArrayList<>();
   //简单的后期处理器实现,理论上应该优先于其他bean的实例化，这里只作为Servlet拦截器使用，所以就作为普通bean一起创建
   private final List<String> postProcessorList = new ArrayList<>();

   public WebMvcAppliactionContext(){
       super();
   }

   public WebMvcAppliactionContext(String contextConfigLocation){
       super();
       if(StringUtils.isNotBlank(contextConfigLocation)){
           this.contextConfigLocation = contextConfigLocation;
       }
   }

    /**
     * 扫描路径
     * @param basePackages
     */
    @Override
    public void scan(String... basePackages) {
        List<String> packages = new ArrayList<>();
        if(StringUtils.isNotBlank(contextConfigLocation)){
            //解析配置文件
            packages = XmlPaser.getbasePackage(contextConfigLocation.split(":")[1]);
        }
        //加上传进来的包
        packages.addAll(Arrays.asList(basePackages));
        scan(packages,null);
    }

    /**
     * 为了解决集合遍历时增加元素重写了方法供内部调用，不太优雅，暂时没想到更好的解决方式
     * @param packages
     * @param beanRegisterIterator
     */
    private void scan(List<String> packages,ListIterator<String> beanRegisterIterator) {
        for(String pack:packages){
            List<String> packList = FileUtil.excuteScanPackage(pack,new ArrayList<>());
            for(String bao:packList){
                doScan(bao,beanRegisterIterator);
            }
        }
    }

    /**
     * 将扫描完的类加入缓存
     * @param basePackages
     * @param beanRegisterIterator
     */
    private void doScan(String basePackages,ListIterator<String> beanRegisterIterator) {
        synchronized (beanRegisterList){
            if(beanRegisterList.contains(basePackages)){
                return;
            }
            if(beanRegisterIterator!= null){
                //解决遍历集合时增加元素的问题
                beanRegisterIterator.add(basePackages);
            }else {
                //第一次扫包时正常添加就行了
                beanRegisterList.add(basePackages);
            }
        }
    }

    /**
     * 注册
     */
    @Override
    public void register() {
        synchronized (beanDefinitionMap){
            ListIterator<String> iterator =  beanRegisterList.listIterator();
            while (iterator.hasNext()){
                String bean = iterator.next();
                try {
                    Class<?> clazz = Class.forName(bean);
                    String name = clazz.getSimpleName().toLowerCase();
                    if(beanDefinitionMap.containsKey(name)){
                        //防止重复注册
                        continue;
                    }
                    if(clazz.isAnnotationPresent(ComponentScan.class)){
                        ComponentScan component = clazz.getAnnotation(ComponentScan.class);
                        String packages = component.value();
                        //手动传入包
                        scan(new ArrayList<String>(){{
                            add(packages);
                        }},iterator);
                        //存入配置类bean定义，并给个出递归的口
                        BeanDefinition beanDefinition = new BeanDefinition(clazz,name,clazz.getDeclaredFields());
                        beanDefinitionMap.put(name,beanDefinition);
                        //递归注册
                        register();
                    }else if(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)){

                        Field[] fields = clazz.getDeclaredFields();
                        BeanDefinition beanDefinition = new BeanDefinition(clazz,name,fields);
                        //存入bean定义
                        beanDefinitionMap.put(name,beanDefinition);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化
     */
    @Override
    public void preInstantiate() {
        for(Map.Entry<String,BeanDefinition> entry:beanDefinitionMap.entrySet()){
            getBean(entry.getKey());
        }
    }

    /**
     * 获取bean
     * @param beanName bean名称
     * @return bean对象
     */
    @Override
    public Object getBean(String beanName) {
        //获取早期bean
        Object bean = getSingletonMap(beanName);
        if(bean != null){
            return bean;
        }
        return getSingleton(beanName);
    }

    /**
     * 获取早期bean对象
     * @param beanName
     * @return
     */
    private Object getSingletonMap(String beanName){
       Object singletonObject = beanMap.get(beanName);
       if (singletonObject == null && readyToCreateBean.contains(beanName)) {
           synchronized (beanMap) {
               singletonObject = earlybeanMap.get(beanName);
           }
       }
       return singletonObject;
   }

    /**
     * bean初始化方法
     * @param beanName
     * @return
     */
    private Object getSingleton(String beanName){
        //防止beanA未实例化时另一个beanA依赖于beanA同时实例化beanA
        synchronized (beanMap){
            Object singletonObject = beanMap.get(beanName);
            if(singletonObject == null){
                if(readyToCreateBean.contains(beanName)){
                    throw new RuntimeException();
                }
                //标记bean正在创建
                beforeSingletonCreate(beanName);
                //bean成功创建标记
                boolean flag = false;
                try {
                    //实例化bean
                    singletonObject = createBeanInstance(beanName,beanDefinitionMap.get(beanName));
                    addEarlybeanMap(beanName,singletonObject);
                    //填充bean属性
                    populateBean(beanName,beanDefinitionMap.get(beanName),singletonObject);
                    flag = true;
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    //无论什么情况创建完后都需要清理正在创建的标记，否则无法释放该bean的创建行为
                    afterCreate(beanName);
                }
                //bean创建成功才需要清理缓存
                if(flag){
                    //一级缓存添加及二级缓存清理
                    addSingleton(beanName,singletonObject);
                }
            }
            return singletonObject;
        }
    }

    /**
     * 实例化对象
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object createBeanInstance(String beanName,BeanDefinition beanDefinition){
        try{
            Class<?> clazz =  beanDefinition.getClazz();
            return clazz.newInstance();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自动装填
     * @param beanName
     * @param beanDefinition
     * @param bean
     */
    private void populateBean(String beanName,BeanDefinition beanDefinition,Object bean){
        Field[] fields = beanDefinition.getFields();
        for (Field field : fields) {
            try {
                if(field.isAnnotationPresent(Autowired.class)){
                    //获取注解中的value值 该值就是bean的name
                    Autowired autowiredAno =  field.getAnnotation(Autowired.class);
                    String dependName = autowiredAno.value();
                    //取消检查机制
                    field.setAccessible(true);
                    //属性注入 获取注入对象bean
                    field.set(bean,getBean(dependName));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 标记bean正在创建
     * @param beanName
     */
    private void beforeSingletonCreate(String beanName){
        readyToCreateBean.add(beanName);
    }

    /**
     * 一级缓存添加
     * @param beanName
     * @param bean
     */
    private void addSingleton(String beanName,Object bean){
        synchronized (beanMap){
            beanMap.put(beanName,bean);
            if(bean instanceof HandlerInterceptor){
                postProcessorList.add(beanName);
            }
            earlybeanMap.remove(beanName);
            initializeCompleteBean.add(beanName);
        }
    }

    /**
     * 二级缓存添加
     * @param beanName
     * @param bean
     */
    private void addEarlybeanMap(String beanName,Object bean){
        synchronized (beanMap) {
            if (!beanMap.containsKey(beanName)) {
                earlybeanMap.put(beanName, bean);
            }
        }
    }

    /**
     * 移除bean正在创建的标记
     * @param beanName
     */
    private void afterCreate(String beanName){
        readyToCreateBean.remove(beanName);
    }

    /**
     * 返回已经创建完成的bean name集合，供外部调用
     * @return
     */
    public List<String> getInitializeBean(){
        return initializeCompleteBean;
    }

    /**
     * 返回已经创建完成的后置处理器集合，供外部调用
     * @return
     */
    public List<String> getPostProcessors(){
        return postProcessorList;
    }
}
