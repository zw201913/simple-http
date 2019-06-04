package com.github.zw201913.simplehttp.core.spring;

import com.github.zw201913.simplehttp.core.DefaultHttpServiceFactory;
import org.springframework.beans.factory.FactoryBean;

/** @author zouwei */
public class HttpFactoryBean<T> implements FactoryBean<T> {

    private static final DefaultHttpServiceFactory httpServiceFactory =
            new DefaultHttpServiceFactory();

    private final Class<T> httpInterface;


    public HttpFactoryBean(Class<T> httpInterface) {
        this.httpInterface = httpInterface;
    }

    @Override
    public T getObject() {
        return httpServiceFactory.getSimpleHttpService(this.httpInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.httpInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
