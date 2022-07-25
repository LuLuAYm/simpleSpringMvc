package com.lulu.springmvc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: cwlu
 * @Date: 2022/07/24/9:08
 * @Description: josn转换工具类
 */
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return OBJECT_MAPPER;
    }

    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
    }

    /**
     * 创建空ObjectNode对象实例
     *
     * @return
     */
    public static ObjectNode createObjectNode() {
        try {
            return OBJECT_MAPPER.createObjectNode();
        } catch (Exception e) {
           e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建空ArrayNode对象实例
     *
     * @return
     */
    public static ArrayNode createArrayNode() {
        try {
            return OBJECT_MAPPER.createArrayNode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 将实体序列化为json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 将实体序列化为格式化的json字符串
     *
     * @param obj
     * @return
     */
    public static String toPrettyJson(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json字符串反序列化为实体 Note:此方法只支持简单实体类型的反序列化,如果需要反序列化为集合类型实体,使用{@link #fromJson(String, Class, Class...) }
     *
     * @param json
     * @param valueClass 实体类型
     * @return
     * @see JsonUtil#fromJson(String, Class, Class...)
     */
    public static <T> T fromJson(String json, Class<T> valueClass) {
        try {
            return OBJECT_MAPPER.readValue(json, valueClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换为 JSON 字符串，忽略空值
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String toJsonIgnoreNull(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 将任意对象（包括null）写为一个JsonNode树模型
     *
     * @param object
     * @return
     */
    public static JsonNode getJsonNodeByObject(Object object) {
        try {
            return OBJECT_MAPPER.valueToTree(object);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JsonNode树模型强转为ObjectNode
     *
     * @param jsonNode
     * @return
     */
    public static ObjectNode getObjectNodeByJsonNode(JsonNode jsonNode) {
        try {
//            return (ObjectNode) OBJECT_MAPPER.readTree(jsonNode.asText());
            return jsonNode.deepCopy();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取指定JsonNode对象某个key的string值
     *
     * @param jsonNode
     * @return
     */
    public static String getStringByJsonNode(JsonNode jsonNode, String keyName) {
        try {
            return jsonNode != null ? (jsonNode.get(keyName) != null ? jsonNode.get(keyName).asText() : "") : "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 将json字符串写为一个JsonNode树模型
     *
     * @param jsonStr
     * @return
     */
    public static JsonNode getJsonNodeByStr(String jsonStr) {
        try {
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 将json字符串反序列化为集合类型实体
     *
     * @param json
     * @param collectionClass  集合类型
     * @param parameterClasses 泛型参数类型
     * @return
     */
    public static <T> T fromJson(String json, Class<?> collectionClass, Class<?>... parameterClasses) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametrizedType(collectionClass, collectionClass, parameterClasses);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json字符串反序列化为List
     *
     * @param json
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        List<T> list = null;
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, beanType);

//            list = OBJECT_MAPPER.readValue(json, new TypeReference<List<T>>() {});
            list = OBJECT_MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将byte[]字节数据序列化为指定类型
     *
     * @param bytes
     * @param ValueClass
     * @param <T>
     * @return
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> ValueClass) {
        T result = null;
        try {
            result = OBJECT_MAPPER.readValue(bytes, ValueClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JsonNode jsonToNode(String json) {
        JsonNode result = null;
        try {
            result = OBJECT_MAPPER.readTree(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JsonNode list2node(List list) throws Exception {
        JsonNode result = null;
        try {
            result = jsonToNode(JsonUtil.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T obj2pojo(Object obj, Class<T> clazz) {
        T result = null;
        try {
            result = OBJECT_MAPPER.convertValue(obj, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String node2Json(JsonNode jsonNode) {
        String result = null;
        try {
            result = OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> List<T> node2List(JsonNode jsonNode, Class<T> clazz) {
        return jsonToList(node2Json(jsonNode), clazz);
    }

    public static <T> Map<String, Object> json2map(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.readValue(jsonString, Map.class);
    }

    /**
     * JsonNode不为空,且里面有元素
     *
     * @param jsonNode
     * @return
     */
    public static boolean isNotEmpty(JsonNode jsonNode) {
        if (jsonNode != null) {
            return jsonNode.fieldNames().hasNext();
        } else {
            return false;
        }
    }

}
