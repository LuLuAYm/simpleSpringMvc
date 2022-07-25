package com.lulu.springmvc.handler;

import com.lulu.springmvc.util.RequestMethod;

import java.lang.reflect.Method;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/22:45
 * @Description: 处理器适配器
 */
public class AnnotationHandler {
    //目标处理器
    private Object bean;
    //处理器目标方法
    private Method method;
    //请求类型保存
    private RequestMethod requestMethod;

    public AnnotationHandler(){
        super();
    }
    public AnnotationHandler(Object bean,Method method,RequestMethod requestMethod){
        super();
        this.bean = bean;
        this.method = method;
        this.requestMethod = requestMethod;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }
}
