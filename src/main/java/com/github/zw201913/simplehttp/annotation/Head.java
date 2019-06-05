package com.github.zw201913.simplehttp.annotation;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.factory.DefaultOkHttpClientFactory;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 注解Head请求方法 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Head {
    /**
     * 请求url
     *
     * @return
     */
    String value() default StringUtils.EMPTY;

    /**
     * 指定创建OkHttpClient的工厂类
     *
     * @return
     */
    Class<? extends BaseOkHttpClientFactory> clientFactory() default
            DefaultOkHttpClientFactory.class;
}
