package com.lulu.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: cwlu
 * @Date: 2022/07/24/0:45
 * @Description:
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value();
}
