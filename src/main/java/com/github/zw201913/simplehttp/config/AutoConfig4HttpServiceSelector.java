package com.github.zw201913.simplehttp.config;

import com.github.zw201913.simplehttp.annotation.EnableSimpleHttp;
import com.github.zw201913.simplehttp.annotation.SimpleHttpService;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Set;

/**
 * 为了创建ClassPathHttpServiceScanner读取包下指定的接口类
 *
 * @author zouwei
 */
public class AutoConfig4HttpServiceSelector
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> packages = Sets.newHashSet();
        AnnotationAttributes annoAttrs =
                AnnotationAttributes.fromMap(
                        importingClassMetadata.getAnnotationAttributes(
                                EnableSimpleHttp.class.getName()));
        String[] basePackages = annoAttrs.getStringArray("value");
        for (String basePackage : basePackages) {
            packages.add(basePackage);
        }
        Class[] basePackageClasses = annoAttrs.getClassArray("basePackageClasses");
        for (Class basePackageClass : basePackageClasses) {
            packages.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (CollectionUtils.isEmpty(packages)) {
            StandardAnnotationMetadata metadata =
                    (StandardAnnotationMetadata) importingClassMetadata;
            packages.add(ClassUtils.getPackageName(metadata.getIntrospectedClass()));
        }
        ClassPathHttpServiceScanner scanner =
                new ClassPathHttpServiceScanner(registry, annoAttrs.getClass("proxyFactoryBean"));
        Optional.ofNullable(resourceLoader).ifPresent(scanner::setResourceLoader);
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(packages));
    }
}
