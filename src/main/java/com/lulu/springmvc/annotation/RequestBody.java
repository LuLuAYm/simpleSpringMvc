package com.lulu.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: cwlu
 * @Date: 2022/07/24/10:06
 * @Description:
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
}
