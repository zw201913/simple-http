package com.github.zw201913.simplehttp.core.proxy;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/** @author zouwei */
@Getter
public class HttpProxyFactory<T> {

    private final Class<T> httpInterface;

    private final Map<Method, HttpMethod> methodCache = Maps.newConcurrentMap();

    public HttpProxyFactory(Class<T> httpInterface) {
        this.httpInterface = httpInterface;
    }

    /**
     * 创建代理对象
     * @param httpProxy
     * @return
     */
    protected T newInstance(HttpProxy<T> httpProxy) {
        return (T)
                Proxy.newProxyInstance(
                        httpInterface.getClassLoader(), new Class[] {httpInterface}, httpProxy);
    }

    /**
     * 创建代理对象
     * @return
     */
    public T newInstance() {
        final HttpProxy<T> mapperProxy = new HttpProxy<>(httpInterface, methodCache);
        return newInstance(mapperProxy);
    }
}
