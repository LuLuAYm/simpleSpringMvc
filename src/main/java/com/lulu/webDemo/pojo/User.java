package com.lulu.webDemo.pojo;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/18:31
 * @Description:
 */
public class User {
   private String name;
   private Integer age;
   private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "name:"+name+"/age:"+age+"/sex:"+sex;
    }
}
