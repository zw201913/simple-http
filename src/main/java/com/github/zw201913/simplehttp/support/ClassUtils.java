package com.github.zw201913.simplehttp.support;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

@Slf4j
public final class ClassUtils {

    private ClassUtils() {}

    /**
     * 是否是数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    /**
     * 是否文件类型
     *
     * @param clazz
     * @return
     */
    public static boolean isFile(Class<?> clazz) {
        return Objects.equals(clazz, File.class);
    }

    /**
     * 是否是文件数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isFileArray(Class<?> clazz) {
        return Objects.equals(clazz, File[].class);
    }

    /**
     * 对象转map
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> obj2Map(Object obj) {
        Map<String, Object> map = Maps.newHashMap();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (IllegalAccessException e) {
            log.error("转换失败", e);
        }
        return map;
    }

    /**
     * 是否是Integer类型
     *
     * @param clazz
     * @return
     */
    public static boolean isInt(Class<?> clazz) {
        return Objects.equals(clazz, Integer.class);
    }
    /**
     * 是否是Integer数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isIntArray(Class<?> clazz) {
        return Objects.equals(clazz, Integer[].class);
    }

    /**
     * 是否是Short类型
     *
     * @param clazz
     * @return
     */
    public static boolean isShort(Class<?> clazz) {
        return Objects.equals(clazz, Short.class);
    }
    /**
     * 是否是Short数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isShortArray(Class<?> clazz) {
        return Objects.equals(clazz, Short[].class);
    }

    /**
     * 是否是Byte类型
     *
     * @param clazz
     * @return
     */
    public static boolean isByte(Class<?> clazz) {
        return Objects.equals(clazz, Byte.class);
    }
    /**
     * 是否是Byte数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isByteArray(Class<?> clazz) {
        return Objects.equals(clazz, Byte[].class);
    }

    /**
     * 是否是Char类型
     *
     * @param clazz
     * @return
     */
    public static boolean isChar(Class<?> clazz) {
        return Objects.equals(clazz, Character.class);
    }
    /**
     * 是否是Char数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isCharArray(Class<?> clazz) {
        return Objects.equals(clazz, Character[].class);
    }

    /**
     * 是否是Long类型
     *
     * @param clazz
     * @return
     */
    public static boolean isLong(Class<?> clazz) {
        return Objects.equals(clazz, Long.class);
    }
    /**
     * 是否是Long数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isLongArray(Class<?> clazz) {
        return Objects.equals(clazz, Long[].class);
    }
    /**
     * 是否是Double类型
     *
     * @param clazz
     * @return
     */
    public static boolean isDouble(Class<?> clazz) {
        return Objects.equals(clazz, Double.class);
    }
    /**
     * 是否是Double数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isDoubleArray(Class<?> clazz) {
        return Objects.equals(clazz, Double[].class);
    }

    /**
     * 是否是Float类型
     *
     * @param clazz
     * @return
     */
    public static boolean isFloat(Class<?> clazz) {
        return Objects.equals(clazz, Float.class);
    }

    /**
     * 是否是Float数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isFloatArray(Class<?> clazz) {
        return Objects.equals(clazz, Float[].class);
    }

    /**
     * 是否是String类型
     *
     * @param clazz
     * @return
     */
    public static boolean isString(Class<?> clazz) {
        return Objects.equals(clazz, String.class);
    }
    /**
     * 是否是String数组类型
     *
     * @param clazz
     * @return
     */
    public static boolean isStringArray(Class<?> clazz) {
        return Objects.equals(clazz, String[].class);
    }

    /**
     * 是否是简单类型
     *
     * @param clazz
     * @return
     */
    public static boolean isSimpleType(Class<?> clazz) {
        return isInt(clazz)
                || isIntArray(clazz)
                || isString(clazz)
                || isStringArray(clazz)
                || isLong(clazz)
                || isLongArray(clazz)
                || isByte(clazz)
                || isByteArray(clazz)
                || isShort(clazz)
                || isShortArray(clazz)
                || isChar(clazz)
                || isCharArray(clazz)
                || isDouble(clazz)
                || isDoubleArray(clazz)
                || isFloat(clazz)
                || isFloatArray(clazz);
    }
}
