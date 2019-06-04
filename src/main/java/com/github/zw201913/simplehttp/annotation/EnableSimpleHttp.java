package com.github.zw201913.simplehttp.annotation;

import com.github.zw201913.simplehttp.config.AutoConfig4HttpServiceSelector;
import com.github.zw201913.simplehttp.core.spring.HttpFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AutoConfig4HttpServiceSelector.class)
@ComponentScan
public @interface EnableSimpleHttp {

    /**
     * 创建代理对象工厂
     *
     * @return
     */
    Class<? extends FactoryBean> proxyFactoryBean() default HttpFactoryBean.class;

    /**
     * 扫描包名
     *
     * @return
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] value() default {};

    /**
     * 扫描类名
     *
     * @return
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] basePackageClasses() default {};
}
