package com.lulu.springmvc.bean;

import java.lang.reflect.Field;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/13:53
 * @Description: 简单bean定义实现
 */
public class BeanDefinition {
    private Class<?> clazz;
    private String name;
    private Field[] fields;


    public BeanDefinition(){
        super();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public BeanDefinition(Class<?> clazz, String name, Field[] fields){
        this.clazz = clazz;
        this.name = name;
        this.fields = fields;
    }
}
