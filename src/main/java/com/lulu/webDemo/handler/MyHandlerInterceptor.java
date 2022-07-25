package com.lulu.webDemo.handler;

import com.lulu.springmvc.annotation.Service;
import com.lulu.springmvc.handler.AnnotationHandler;
import com.lulu.springmvc.handler.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: cwlu
 * @Date: 2022/07/24/14:00
 * @Description: 拦截器示例
 */
@Service
public class MyHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, AnnotationHandler handler) throws Exception {
        System.out.println("preHandle init...........................................");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public Object postHandle(HttpServletRequest request, HttpServletResponse response, AnnotationHandler handler, Object result) throws Exception {
        System.out.println("postHandle init...........................................");
        return HandlerInterceptor.super.postHandle(request, response, handler, result);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, AnnotationHandler handler, Exception ex) throws Exception {
        System.out.println("afterCompletion init...........................................");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
