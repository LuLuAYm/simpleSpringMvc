package com.lulu.springmvc.util;

import com.lulu.webDemo.pojo.User;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cwlu
 * @Date: 2022/07/24/12:31
 * @Description: List工具类
 */
public class ListUtil {
    /**
     * 获取参数泛型
     * @param curFieldType
     * @return
     * @throws Exception
     */
    public static Class<?> getListGenericType(Class<?> curFieldType) throws Exception{
        //判断是否集合类型
        if (isTypeForList(curFieldType)) {
            Type genericType = curFieldType.getGenericSuperclass();
                if (genericType instanceof ParameterizedType) {
                    // 当前集合的泛型类型
                    ParameterizedType pt = (ParameterizedType) genericType;
                    // 得到泛型里的class类型对象
                    Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[0];
                    return actualTypeArgument;
                }
        }
        //没找到就返回Object
        return Object.class;
    }

    /**
     * 判断是否是集合类型
     * @param clazz
     * @return
     * @throws Exception
     */
    public static boolean isTypeForList(Class<?> clazz) throws Exception{
        return List.class.isAssignableFrom(clazz) || clazz.newInstance() instanceof List;
    }

    /**
     * 获取参数泛型2 事实证明还是硬编码好用
     * @param parameter
     * @return
     * @throws Exception
     */
    public static Class<?> getListGenericType(Parameter parameter) throws Exception{
        String listName = parameter.getParameterizedType().getTypeName();
        String forName = listName.substring(listName.indexOf("<")+1,listName.indexOf(">"));
        if(StringUtils.isBlank(forName)){
            return Object.class;
        }
        return Class.forName(forName);
    }

    /**
     * 集合判空
     * @param list
     * @return
     */
    public static boolean isEmpty(List list){
        return list != null && list.size() > 0;
    }

}
