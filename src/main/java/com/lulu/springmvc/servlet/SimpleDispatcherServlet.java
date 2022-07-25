package com.lulu.springmvc.servlet;

import com.lulu.springmvc.annotation.*;
import com.lulu.springmvc.context.ApplicationContext;
import com.lulu.springmvc.context.impl.WebMvcAppliactionContext;
import com.lulu.springmvc.handler.AnnotationHandler;
import com.lulu.springmvc.handler.HandlerInterceptor;
import com.lulu.springmvc.util.HttpUtil;
import com.lulu.springmvc.util.JsonUtil;
import com.lulu.springmvc.util.ListUtil;
import com.lulu.springmvc.util.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @Author: cwlu
 * @Date: 2022/07/22/23:54
 * @Description: 前端控制器简单实现（其实兼具了ApplactionContext的功能），这里只实现了注解的处理器适配
 */
public class SimpleDispatcherServlet extends HttpServlet {

    private static WebMvcAppliactionContext webApplicationContext;
    //处理器映射器
    private static Map<String,List<AnnotationHandler>> handlerMap = new HashMap();
    //拦截器 这里没有做排序，需要容器加个Order注解实现sort，后面优化吧。。。
    private final List<HandlerInterceptor> handlerInterceptorList = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        super.init();
        //加载容器配置文件路径
        String contextConfigLocation = this.getServletConfig().getInitParameter("contextConfigLocation");
        //实例化web容器
        this.webApplicationContext = new WebMvcAppliactionContext(contextConfigLocation);
        //初始化容器
        onRefresh(webApplicationContext);
        //初始化处理器映射器
        initHandlerMapping();
        //初始化拦截器
        initHandlerInterceptor();
    }

    /**
     * 刷新mvc容器
     * @param applicationContext
     */
    public void onRefresh(ApplicationContext applicationContext){
        //配置文件扫描
        applicationContext.scan();
        //注册bean定义
        applicationContext.register();
        //bean初始化
        applicationContext.preInstantiate();
    }

    /**
     * 初始化handlerMap
     */
    public void initHandlerMapping(){
        //获取容器bean name集合
        List<String> beanNames = webApplicationContext.getInitializeBean();
        for (String beanName:beanNames){
            //获取bean对象
           Object bean =  webApplicationContext.getBean(beanName);
            Class<?> clazz = bean.getClass();
            //对Controller才做映射
            if(clazz.isAnnotationPresent(Controller.class)){
                Method[] methods =  clazz.getDeclaredMethods();
                for (Method method : methods) {
                    //有RequestMapping注解说明符合映射规则
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        //url路径
                        String url = requestMapping.value();
                        if(!handlerMap.containsKey(url)){
                            RequestMethod requestMethod = requestMapping.method();
                            List<AnnotationHandler> annotationHandlers = new ArrayList<>();
                            AnnotationHandler annotationHandler = new AnnotationHandler(bean,method,requestMethod);
                            annotationHandlers.add(annotationHandler);
                            //建立url与处理器适配器映射关系
                            handlerMap.put(url,annotationHandlers);
                        }else {
                            //说明相同的url有不同的请求方式
                            RequestMethod requestMethod = requestMapping.method();
                            List<AnnotationHandler> annotationHandlers = handlerMap.get(url);
                            AnnotationHandler annotationHandler = new AnnotationHandler(bean,method,requestMethod);
                            annotationHandlers.add(annotationHandler);
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化拦截器
     */
    public void initHandlerInterceptor(){
        //获取拦截器名称集合
        List<String> beanNames = webApplicationContext.getPostProcessors();
        for (String beanName:beanNames){
            //获取bean对象
            Object bean = webApplicationContext.getBean(beanName);
            if(bean instanceof HandlerInterceptor){
                //注册拦截器
                handlerInterceptorList.add((HandlerInterceptor)bean);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatcher(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatcher(req,resp);
    }

    /**
     * 请求处理
     * @param req
     * @param resp
     */
    public void doDispatcher(HttpServletRequest req, HttpServletResponse resp){
        //获取处理器适配器
        AnnotationHandler annotationHandler = getHandler(req);
        resp.setContentType("text/html;charset=utf-8");
        //保存异常对象
        Exception dispatchException = null;
        try {
            //如果没有找到适配器获取拦截器都校验不通过则返回404
            if(annotationHandler == null || !applyPreHandle(req,resp,annotationHandler)){
                //没有找到适配器返回404
//                OutputStream ps = resp.getOutputStream();
//                resp.getWriter().print( "网页走丢了");
//                ps.write("网页走丢了".getBytes("UTF-8"));
                req.getRequestDispatcher("/404.jsp").forward(req,resp);
                return;
            }
            //调用处理器目标方法
            Object result = annotationHandler.getMethod().invoke(annotationHandler.getBean(),getMethodParam(req,resp,annotationHandler));
            //调用拦截修改返回值
            result = applyPostHandle(req,resp,annotationHandler,result);
            if(result instanceof String){
                //跳转JSP
                if(((String) result).contains(":")){
                    //获取forward和
                    String type=((String) result).split(":")[0];
                    String view=((String) result).split(":")[1];
                    if(type.equals("forward")){
                        //转发
                        req.getRequestDispatcher("/"+view+".jsp").forward(req,resp);
                    }else if(type.equals("redirect")){
                        //重定向
                        resp.sendRedirect("/"+view+".jsp");
                    }else{
                        //默认转发
                        req.getRequestDispatcher("/"+view+".jsp").forward(req,resp);
                    }
                }else{
                    //默认转发
                    req.getRequestDispatcher("/"+((String) result)+".jsp").forward(req,resp);
                }
            }else if(annotationHandler.getMethod().isAnnotationPresent(ResponseBody.class)) {
                //返回json格式数据
                String json = JsonUtil.toJson(result);
                PrintWriter writer = resp.getWriter();
                writer.print(json);
                writer.flush();
                writer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            //赋值异常
            dispatchException = e;
        }finally {
            //调用拦截器做后置处理，如果有异常则处理异常
            processDispatchResult(req,resp,annotationHandler,dispatchException);
        }
    }

    /**
     * 获取处理器适配器
     * @param req
     * @return
     */
    public AnnotationHandler getHandler(HttpServletRequest req){
        String requestURI = req.getServletPath();
        //获取处理器适配器集合
        List<AnnotationHandler> annotationHandler = handlerMap.get(requestURI);
        if(annotationHandler != null){
            for (AnnotationHandler ah:annotationHandler){
                //判断是否存在请求类型
                if(ah.getRequestMethod().getMethod().equals(req.getMethod())){
                    return ah;
                }
            }
        }
        return null;
    }

    /**
     * 返回处理器目标方法参数集合
     * @param req
     * @param resp
     * @param annotationHandler
     * @return
     * @throws Exception
     */
    public Object[] getMethodParam(HttpServletRequest req,HttpServletResponse resp,AnnotationHandler annotationHandler) throws Exception{
        //获取方法参数对象集合
        Parameter[] params = annotationHandler.getMethod().getParameters();
        Object[] objects = {};
        List<Object> parameterList = new ArrayList<>();
        if(annotationHandler.getRequestMethod().getMethod().equals("GET")){
            //获取get请求参数Map
            Map<String, String> requestParam =  HttpUtil.getRequestParam(req);
            for(Parameter parameter:params){
                String name = "";
                //获取参数类型
                Class type = parameter.getType();
                //如果使用了RequestParam注解，则用value作为参数名映射，否则使用反射获取参数名
                if(parameter.isAnnotationPresent(RequestParam.class)){
                    name = parameter.getAnnotation(RequestParam.class).value();
                }else {
                    name = parameter.getName();
                }
                String value = requestParam.get(name);
                if(value != null){
                    //非String类型转换（不完善）,需要专门写个类做规则处理,这里做了简单实现
                    if(type.equals(Integer.class)){
                        parameterList.add(Integer.parseInt(value));
                    }else if(type.equals(Long.class)){
                        parameterList.add(Long.parseLong(value));
                    }else if(type.equals(Date.class)){
                        parameterList.add(new Date(value));
                    }else if(type.equals(HttpServletRequest.class)){
                        parameterList.add(req);
                    }else if(type.equals(HttpServletResponse.class)){
                        parameterList.add(resp);
                    }else {
                        parameterList.add(value);
                    }
                }else {
                    parameterList.add(null);
                }
            }
        }else {
            //获取post请求参数json串
            String reqJson =  HttpUtil.postRequestParam(req);
            for(Parameter parameter:params){
                //获取参数类型
                Class type = parameter.getType();
                if(parameter.isAnnotationPresent(RequestBody.class)){
                    if(ListUtil.isTypeForList(type)){
                        //这个没用了，获取泛型是空值返回默认object
//                        Class genericType = ListUtil.getListGenericType(type);
                        //获取集合泛型class
                        parameterList.add(JsonUtil.jsonToList(reqJson,ListUtil.getListGenericType(parameter)));
                    }else {
                        parameterList.add(JsonUtil.fromJson(reqJson,type));
                    }
                }else if(type.equals(HttpServletRequest.class)){
                    parameterList.add(req);
                }else if(type.equals(HttpServletResponse.class)){
                    parameterList.add(resp);
                }else {
                    parameterList.add(null);
                }
            }
        }
        //将集合转成数组
        if(ListUtil.isEmpty(parameterList)){
            objects = parameterList.toArray();
        }
        return objects;
    }

    /**
     * 拦截器请求拦截处理
     * @param req
     * @param resp
     * @param annotationHandler
     * @return
     */
    private boolean applyPreHandle(HttpServletRequest req, HttpServletResponse resp,AnnotationHandler annotationHandler) throws Exception{
        for(HandlerInterceptor handlerInterceptor:handlerInterceptorList){
            //如果有一个被拦截则直接校验不通过
            if(!handlerInterceptor.preHandle(req,resp,annotationHandler)) return false;
        }
        return true;
    }

    /**
     * 拦截器返回值修改处理
     * @param req
     * @param resp
     * @param annotationHandler
     * @return
     */
    private Object applyPostHandle(HttpServletRequest req, HttpServletResponse resp,AnnotationHandler annotationHandler,Object result) throws Exception{
        for(HandlerInterceptor handlerInterceptor:handlerInterceptorList){
            //没有做排序，所以返回随缘了= =
            result = handlerInterceptor.postHandle(req,resp,annotationHandler,result);
        }
        return result;
    }

    /**
     * 拦截器异常处理
     * @param req
     * @param resp
     * @param annotationHandler
     * @return
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp,AnnotationHandler annotationHandler,Exception exception){
        for(HandlerInterceptor handlerInterceptor:handlerInterceptorList){
            //没有做排序，处理也随缘
            try {
                handlerInterceptor.afterCompletion(req,resp,annotationHandler,exception);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
