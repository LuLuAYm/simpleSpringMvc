package com.lulu.springmvc.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: cwlu
 * @Date: 2022/07/24/0:15
 * @Description: http请求工具类
 */
public class HttpUtil {
    /**
     * 返回get请求参数map
     * @param request
     * @return
     */
    public static Map<String, String> getRequestParam( HttpServletRequest request) {
        Map<String, String> result = new HashMap<>(16);
        // 获取URL的传参
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paraName = params.nextElement();
            String[] paraValues = request.getParameterValues(paraName);
            result.put(paraName, paraValues.length == 1 ? paraValues[0] : paraValues[paraValues.length - 1]);
        }
        return result;
    }

    /**
     * 返回post请求参数json串
     * @param request
     * @return
     * @throws IOException
     */
    public static String postRequestParam(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        String str;
        StringBuilder wholeStr = new StringBuilder();
        while((str = br.readLine()) != null){
            wholeStr.append(str);
        }
        return wholeStr.toString();
    }

}
