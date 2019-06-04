package com.github.zw201913.simplehttp.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.util.Objects;

/** @author zouwei */
@Slf4j
public final class JsonUtils {

    /** 防止使用者直接new JsonUtil() */
    private JsonUtils() {}

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对象所有字段全部列入序列化
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        /** 所有日期全部格式化成时间戳 因为即使指定了DateFormat，也不一定能满足所有的格式化情况，所以统一为时间戳，让使用者按需转换 */
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
        /** 忽略空Bean转json的错误 假设只是new方式创建对象，并且没有对里面的属性赋值，也要保证序列化的时候不报错 */
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        /** 忽略反序列化中json字符串中存在，但java对象中不存在的字段 */
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2String(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
            // 即使序列化出错，也要保证程序走下去
            return null;
        }
    }

    /**
     * 对象转json字符串(带美化效果)
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2StringPretty(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return obj instanceof String
                    ? (String) obj
                    : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
            // 即使序列化出错，也要保证程序走下去
            return null;
        }
    }

    /**
     * json字符串转简单对象
     *
     * @param <T>
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T string2Obj(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json) || Objects.isNull(clazz)) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) json : objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            // 即使序列化出错，也要保证程序走下去
            return null;
        }
    }

    /**
     * json字符串转复杂对象
     *
     * @param json
     * @param typeReference 例如：new TypeReference<List<User>>(){}
     * @param <T> 例如：List<User>
     * @return
     */
    public static <T> T string2Obj(String json, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(json) || Objects.isNull(typeReference)) {
            return null;
        }
        try {
            return (T)
                    (typeReference.getType().equals(String.class)
                            ? (T) json
                            : objectMapper.readValue(json, typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            // 即使序列化出错，也要保证程序走下去
            return null;
        }
    }

    /**
     * json字符串转复杂对象
     *
     * @param json
     * @param collectionClass 例如：List.class
     * @param elementClasses 例如：User.class
     * @param <T> 例如：List<User>
     * @return
     */
    public static <T> T string2Obj(
            String json, Class<?> collectionClass, Class<?>... elementClasses) {
        if (StringUtils.isEmpty(json)
                || Objects.isNull(collectionClass)
                || Objects.isNull(elementClasses)) {
            return null;
        }
        JavaType javaType =
                objectMapper
                        .getTypeFactory()
                        .constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            // 即使序列化出错，也要保证程序走下去
            return null;
        }
    }
}
