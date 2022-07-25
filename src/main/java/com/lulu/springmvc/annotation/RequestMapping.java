package com.lulu.springmvc.annotation;

import com.lulu.springmvc.util.RequestMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value();
    RequestMethod method();
}
