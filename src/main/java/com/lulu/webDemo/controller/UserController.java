package com.lulu.webDemo.controller;

import com.lulu.springmvc.annotation.*;
import com.lulu.springmvc.util.RequestMethod;
import com.lulu.webDemo.pojo.User;
import com.lulu.webDemo.service.impl.Service1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/18:33
 * @Description: 处理器
 */
@Controller
public class UserController {

    @Autowired("service1")
    private Service1 service1;

    //循环依赖验证
    public void get(){
        service1.init();
    }

    //定义方法
    @RequestMapping(value = "/findUser",method = RequestMethod.GET)
    public String findUser(String name,Integer age){
        System.out.println("name:"+name+"/Integer:"+age);
        return "success";
    }

    //定义方法
    @RequestMapping(value = "/findUser",method = RequestMethod.POST)
    public @ResponseBody User findUserPost(HttpServletRequest req, HttpServletResponse resp, @RequestBody List<User> user){
        System.out.println("req:"+req+"/resp:"+resp);
        System.out.println(user.get(0));
        return user.get(0);
    }
}
