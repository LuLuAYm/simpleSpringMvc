package com.lulu.webDemo;

import com.lulu.springmvc.context.impl.WebMvcAppliactionContext;
import com.lulu.webDemo.controller.UserController;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/18:43
 * @Description: 测试mvc容器的自动注入，测试servlet需要通过tomcat插件启动
 */
public class Start {
    public static void main(String[]args){
        WebMvcAppliactionContext webMvcAppliactionContext = new WebMvcAppliactionContext();
        webMvcAppliactionContext.scan("com.lulu.webDemo");
        webMvcAppliactionContext.register();
        webMvcAppliactionContext.preInstantiate();
        UserController userController = (UserController) webMvcAppliactionContext.getBean("usercontroller");
        userController.get();
    }
}
