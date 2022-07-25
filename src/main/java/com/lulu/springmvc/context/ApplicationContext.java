package com.lulu.springmvc.context;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/0:27
 * @Description:
 */
public interface ApplicationContext {
    void scan(String... basePackages);
    void register();
    void preInstantiate();
    Object getBean(String beanName);
}
