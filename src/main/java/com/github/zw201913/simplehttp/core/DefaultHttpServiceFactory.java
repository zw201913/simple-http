package com.github.zw201913.simplehttp.core;

import com.github.zw201913.simplehttp.core.proxy.HttpProxyFactory;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

/**
 * 通过类名获取代理对象
 *
 * @author zouwei
 */
public class DefaultHttpServiceFactory implements HttpServiceFactory {

    /** 缓存代理对象 */
    private static final Map<Class<?>, Object> proxyCache = Maps.newConcurrentMap();

    /**
     * 获取代理对象
     *
     * @param type
     * @param <T>
     * @return
     */
    @Override
    public <T> T getSimpleHttpService(Class<T> type) {
        return cacheProxy(type);
    }

    /**
     * 缓存代理对象
     *
     * @param type
     * @param <T>
     * @return
     */
    private <T> T cacheProxy(Class<T> type) {
        T proxy = (T) proxyCache.get(type);
        if (Objects.isNull(proxy)) {
            proxy = new HttpProxyFactory<>(type).newInstance();
            proxyCache.put(type, proxy);
        }
        return proxy;
    }
}
