package com.github.zw201913.simplehttp.support;

import org.apache.commons.lang3.StringUtils;

/** Created by zouwei on 2017/11/8. */
public class CastUtils {
    private CastUtils() {}

    /**
     * 转为字符串值（默认值为""）
     *
     * @param obj
     * @return String
     * @author:zouwei
     */
    public static String castString(final Object obj) {
        return castString(obj, "");
    }

    /**
     * 转为字符串值（提供默认值）
     *
     * @param obj
     * @param defaultValue
     * @return String
     * @author:zouwei
     */
    public static String castString(final Object obj, final String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转为int值（默认值为0）
     *
     * @param obj
     * @return int
     * @author:zouwei
     */
    public static int castInt(final Object obj) {
        return castInt(obj, 0);
    }

    /**
     * 转为int值（提供默认值）
     *
     * @param obj
     * @param defaultValue
     * @return int
     * @author:zouwei
     */
    public static int castInt(final Object obj, final int defaultValue) {
        int intValue = defaultValue;
        if (obj != null) {
            final String strValue = CastUtils.castString(obj);
            if (!StringUtils.isEmpty(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (final NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    /**
     * 转为double值（默认值为0）
     *
     * @param obj
     * @return double
     * @author:zouwei
     */
    public static double castDouble(final Object obj) {
        return castDouble(obj, 0);
    }

    /**
     * 转为double值（提供默认值）
     *
     * @param obj
     * @param defaultValue
     * @return double
     * @author:zouwei
     */
    public static double castDouble(final Object obj, final double defaultValue) {
        double doubleValue = defaultValue;
        if (obj != null) {
            final String strValue = CastUtils.castString(obj);
            if (!StringUtils.isEmpty(strValue)) {
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (final NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }

    /**
     * 转为long值（默认值为0）
     *
     * @param obj
     * @return long
     * @author:zouwei
     */
    public static long castLong(final Object obj) {
        return castLong(obj, 0);
    }

    /**
     * 转为long值（提供默认值）
     *
     * @param obj
     * @param defaultValue
     * @return long
     * @author:zouwei
     */
    public static long castLong(final Object obj, final long defaultValue) {
        long longValue = defaultValue;
        if (obj != null) {
            final String strValue = CastUtils.castString(obj);
            if (StringUtils.isEmpty(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (final NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    /**
     * 转为boolean值（默认值为false）
     *
     * @param obj
     * @return boolean
     * @author:zouwei
     */
    public static boolean castBoolean(final Object obj) {
        return castBoolean(obj, false);
    }

    /**
     * 转为boolean值（提供默认值）
     *
     * @param obj
     * @param defaultValue
     * @return boolean
     * @author:zouwei
     */
    public static boolean castBoolean(final Object obj, final boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (obj != null) {
            final String strValue = CastUtils.castString(obj);
            booleanValue = Boolean.parseBoolean(strValue);
        }
        return booleanValue;
    }
}
