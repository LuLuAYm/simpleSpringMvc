package com.lulu.webDemo.service.impl;

import com.lulu.springmvc.annotation.Autowired;
import com.lulu.springmvc.annotation.Service;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/18:36
 * @Description:
 */
@Service
public class Service2 {
    @Autowired("service1")
    Service1 service1;

    public void init(){
        System.out.println("go in service2 init...");
        service1.getUser();
    }
}
