package com.lulu.springmvc.util;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/23:05
 * @Description: 请求类型
 */
public enum RequestMethod {
    GET("GET"),
    HEAD("GHEADET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");
    String name;

    private RequestMethod(String name) {
        this.name= name;
    }

    public String getMethod(){
        return name;
    }
}
