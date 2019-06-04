package com.github.zw201913.simplehttp.core.proxy;

import com.github.zw201913.simplehttp.annotation.*;
import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
public class HttpProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = 8821931376901660733L;

    /** okHttpClient缓存 */
    private static final Map<Class<? extends BaseOkHttpClientFactory>, BaseOkHttpClientFactory>
            okHttpClientFactoryCache = Maps.newConcurrentMap();

    private final Class<T> httpInterface;
    private final Map<Method, HttpMethod> httpCache;

    public HttpProxy(Class<T> httpInterface, Map<Method, HttpMethod> httpCache) {
        this.httpInterface = httpInterface;
        this.httpCache = httpCache;
    }

    /**
     * 调用代理方法
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                throw t;
            }
        }
        final HttpMethod httpMethod = cachedMapperMethod(method);
        return httpMethod.execute(args);
    }

    /**
     * 缓存HttpMethod
     *
     * @param method
     * @return
     */
    private HttpMethod cachedMapperMethod(Method method) throws Exception {
        HttpMethod httpMethod = httpCache.get(method);
        if (Objects.isNull(httpMethod)) {
            HttpMethodType httpMethodType = resolveMethodAnnotation(method);
            httpMethod = new HttpMethod(method, this, httpMethodType);
            httpCache.put(method, httpMethod);
        }
        return httpMethod;
    }

    /**
     * 解析方法上面的注解
     *
     * @param method
     * @return
     * @throws Exception
     */
    private HttpMethodType resolveMethodAnnotation(Method method) throws Exception {
        // 获取方法上所有的注解
        Annotation[] allAnnotations = method.getDeclaredAnnotations();
        if (Objects.isNull(allAnnotations) || allAnnotations.length <= 0) {
            return null;
        }
        for (Annotation annotation : allAnnotations) {
            Class<? extends Annotation> clazz = annotation.annotationType();
            if (Objects.equals(clazz, Get.class)) {
                Get get = method.getDeclaredAnnotation(Get.class);
                return new HttpMethodType(
                        get.value(),
                        cacheOkHttpClientFactory(get.clientFactory()),
                        null,
                        HttpMethodType.MethodType.GET);
            } else if (Objects.equals(clazz, Head.class)) {
                Head head = method.getDeclaredAnnotation(Head.class);
                return new HttpMethodType(
                        head.value(),
                        cacheOkHttpClientFactory(head.clientFactory()),
                        null,
                        HttpMethodType.MethodType.HEAD);
            } else if (Objects.equals(clazz, Post.class)) {
                Post post = method.getDeclaredAnnotation(Post.class);
                return new HttpMethodType(
                        post.value(),
                        cacheOkHttpClientFactory(post.clientFactory()),
                        post.handler(),
                        HttpMethodType.MethodType.POST);
            } else if (Objects.equals(clazz, Put.class)) {
                Put put = method.getDeclaredAnnotation(Put.class);
                return new HttpMethodType(
                        put.value(),
                        cacheOkHttpClientFactory(put.clientFactory()),
                        put.handler(),
                        HttpMethodType.MethodType.PUT);
            } else if (Objects.equals(clazz, Delete.class)) {
                Delete delete = method.getDeclaredAnnotation(Delete.class);
                return new HttpMethodType(
                        delete.value(),
                        cacheOkHttpClientFactory(delete.clientFactory()),
                        delete.handler(),
                        HttpMethodType.MethodType.DELETE);
            } else if (Objects.equals(clazz, Patch.class)) {
                Patch patch = method.getDeclaredAnnotation(Patch.class);
                return new HttpMethodType(
                        patch.value(),
                        cacheOkHttpClientFactory(patch.clientFactory()),
                        patch.handler(),
                        HttpMethodType.MethodType.PATCH);
            }
        }
        return null;
    }

    /**
     * 缓存OkHttpClientFactory
     *
     * @param clientFactory
     * @return
     * @throws Exception
     */
    private BaseOkHttpClientFactory cacheOkHttpClientFactory(
            Class<? extends BaseOkHttpClientFactory> clientFactory) throws Exception {
        BaseOkHttpClientFactory okHttpClientFactory = okHttpClientFactoryCache.get(clientFactory);
        if (Objects.isNull(okHttpClientFactory)) {
            try {
                okHttpClientFactory = clientFactory.getDeclaredConstructor().newInstance();
                okHttpClientFactoryCache.put(clientFactory, okHttpClientFactory);
            } catch (Exception e) {
                log.error("创建OkHttpClient失败", e);
                throw e;
            }
        }
        return okHttpClientFactory;
    }
}
