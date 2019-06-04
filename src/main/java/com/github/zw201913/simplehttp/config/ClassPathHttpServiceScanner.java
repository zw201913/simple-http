package com.github.zw201913.simplehttp.config;

import com.github.zw201913.simplehttp.annotation.SimpleHttpService;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Iterator;
import java.util.Set;

/**
 * 读取指定包下的接口类，并且指定FactoryBean生成代理对象
 *
 * @author zouwei
 */
@Setter
public class ClassPathHttpServiceScanner extends ClassPathBeanDefinitionScanner {

    private Class<? extends FactoryBean> httpFactoryBeanClass;

    public ClassPathHttpServiceScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public ClassPathHttpServiceScanner(
            BeanDefinitionRegistry registry, Class<? extends FactoryBean> httpFactoryBeanClass) {
        this(registry);
        this.httpFactoryBeanClass = httpFactoryBeanClass;
    }

    /** 注册过滤取 */
    public void registerFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(SimpleHttpService.class));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && metadata.isIndependent();
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 扫描指定的包
     *
     * @param basePackages
     * @return
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (!beanDefinitions.isEmpty()) {
            this.processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    /**
     * 处理BeanDefinition指定FactoryBean
     *
     * @param beanDefinitions
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        Iterator iterator = beanDefinitions.iterator();
        while (iterator.hasNext()) {
            BeanDefinitionHolder holder = (BeanDefinitionHolder) iterator.next();
            GenericBeanDefinition beanDefinition =
                    (GenericBeanDefinition) holder.getBeanDefinition();
            beanDefinition
                    .getConstructorArgumentValues()
                    .addGenericArgumentValue(beanDefinition.getBeanClassName());
            beanDefinition.setBeanClass(httpFactoryBeanClass);
            // 通过类型查找
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }
}
