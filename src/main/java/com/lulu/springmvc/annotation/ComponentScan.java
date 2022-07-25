package com.lulu.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/15:24
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScan {
    String value();
}
