package com.lulu.springmvc.handler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: cwlu
 * @Date: 2022/07/24/10:39
 * @Description: 请求拦截器
 */
public interface HandlerInterceptor {

    //可以修改拦截请求
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, AnnotationHandler handler) throws Exception {
        return true;
    }

    //可以修改UserController处理结果
    default Object postHandle(HttpServletRequest request, HttpServletResponse response, AnnotationHandler handler,Object result) throws Exception {
        return result;
    }

    //可以修改响应 request response及对异常处理
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, AnnotationHandler handler,Exception ex) throws Exception {
    }
}
