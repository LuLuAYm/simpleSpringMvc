package com.lulu.springmvc.util;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cwlu
 * @Date: 2022/07/23/14:55
 * @Description: Xml解析器
 */
public class XmlPaser {


    public static List<String> getbasePackage(String xml){
        List<String> list = new ArrayList<>();
        try {
            SAXReader saxReader=new SAXReader();
            InputStream inputStream = XmlPaser.class.getClassLoader().getResourceAsStream(xml);
            //XML文档对象
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            Element componentScans = rootElement.element("component-scans");
            Attribute attribute = componentScans.attribute("base-package");
            String basePackage = attribute.getText();
            if(StringUtils.isNotBlank(basePackage)){
                list.add(basePackage);
            }
            List<Element> componentScanList = componentScans.elements("component-scan");
            for(Element component:componentScanList){
                Attribute attributes =component.attribute("package");
                String basePackages = attributes.getText();
                if(StringUtils.isNotBlank(basePackages)){
                    list.add(basePackages);
                }
            }
            return list;
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
        }
        return list;
    }

}
