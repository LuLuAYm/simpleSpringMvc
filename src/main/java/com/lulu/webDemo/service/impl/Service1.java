package com.lulu.webDemo.service.impl;

import com.lulu.springmvc.annotation.Autowired;
import com.lulu.springmvc.annotation.Service;
import com.lulu.webDemo.pojo.User;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/18:35
 * @Description:
 */
@Service
public class Service1 {
    @Autowired("service2")
    Service2 service2;

    public void init(){
        System.out.println("go in Service1 init...");
        service2.init();
    }

    public void getUser(){
        User user = new User();
        user.setName("lulu");
        user.setAge(26);
        user.setSex("男");
        System.out.println(user.toString());
    }
}
