package com.lulu.springmvc.util;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/14:48
 * @Description: 解析包工具类
 */
public class FileUtil {
    /**
     * 扫描包
     */
    public static List<String> excuteScanPackage(String pack, List <String> list){
        if(list == null){
            list = new ArrayList<>();
        }
        URL url = FileUtil.class.getClassLoader().getResource("");
        //解决中文路径乱码问题
        String path = URLDecoder.decode(url.getPath()+pack.replaceAll("\\.", "/"));
        File dir=new File(path);
        File[] dirList= dir.listFiles();
        if(dirList == null){
            //传进来的就是个类
            //文件目录下文件  获取全路径
            String className=pack+"."+dir.getName().replaceAll(".class","");
            list.add(className);
        }else {
            for(File f:dirList){
                if(f.isDirectory()){
                    //递归
                    excuteScanPackage(pack+"."+f.getName(),list);
                }else{
                    String className=pack+"."+f.getName().replaceAll(".class","");
                    list.add(className);
                }
            }
        }
        return list;
    }
}
