package com.lulu.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/10:06
 * @Description:
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
}
