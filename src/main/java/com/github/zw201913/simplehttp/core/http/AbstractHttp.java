package com.github.zw201913.simplehttp.core.http;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.handler.RequestParamsHandler;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/** @author zouwei */
@Slf4j
public abstract class AbstractHttp {

    private final BaseOkHttpClientFactory okHttpClientFactory;

    /**
     * 构造函数
     *
     * @param okHttpClientFactory
     */
    public AbstractHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        this.okHttpClientFactory = okHttpClientFactory;
    }

    /**
     * 创建OkHttpClient
     *
     * @return
     */
    public OkHttpClient okHttpClient() {
        return okHttpClientFactory.okHttpClient();
    }
    /**
     * 创建Call
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    private Call newCall(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        if (Objects.equals(this.getClass(), WebSocketFactory.class)) {
            throw new RuntimeException("WebSocket请求方式错误");
        }
        return okHttpClient().newCall(newRequest(url, headers, params, files, progressListener));
    }
    /**
     * 发送请求
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     * @throws IOException
     */
    public Response send(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener)
            throws IOException {
        return newCall(url, headers, params, files, progressListener).execute();
    }

    /**
     * 发送请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     * @throws IOException
     */
    public Response send(String url, Map<String, String> headers, Map<String, Object> params)
            throws IOException {
        return send(url, headers, params, null, null);
    }

    /**
     * 发送请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public Response send(String url, Map<String, Object> params) throws IOException {
        return send(url, null, params);
    }

    /**
     * 发送请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Response send(String url) throws IOException {
        return send(url, null, null);
    }

    /**
     * 发送异步请求
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param responseCallback
     */
    public void sendAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        newCall(url, headers, params, files, progressListener).enqueue(responseCallback);
    }

    /**
     * 发送异步请求
     *
     * @param url
     * @param headers
     * @param params
     * @param responseCallback
     */
    public void sendAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        sendAsync(url, headers, params, null, null, responseCallback);
    }

    /**
     * 发送异步请求
     *
     * @param url
     * @param params
     * @param responseCallback
     */
    public void sendAsync(String url, Map<String, Object> params, Callback responseCallback) {
        sendAsync(url, null, params, responseCallback);
    }

    /**
     * 发送异步请求
     *
     * @param url
     * @param responseCallback
     */
    public void sendAsync(String url, Callback responseCallback) {
        sendAsync(url, null, responseCallback);
    }

    /**
     * 创建请求
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    private Request newRequest(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        Request.Builder builder = new Request.Builder();
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((key, value) -> builder.addHeader(key, value));
        }
        enhanceRequestBuilder(builder, url, params, files, progressListener);
        enhanceUrl(builder, url, params);
        return builder.build();
    }

    /**
     * 增强url
     *
     * @param builder
     * @param url
     */
    protected void enhanceUrl(Request.Builder builder, String url, Map<String, Object> params) {
        builder.url(url);
    }

    /**
     * 扩展Request.Builder
     *
     * @param builder
     * @param url
     * @param params
     * @param files
     * @param progressListener
     */
    protected abstract void enhanceRequestBuilder(
            Request.Builder builder,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener);

    /** 存放RequestParamsHandler */
    private static final Map<Class<? extends RequestParamsHandler>, RequestParamsHandler>
            handlerMap = Maps.newConcurrentMap();

    /**
     * 提前注册RequestParamsHandler
     *
     * @param clazz
     */
    public static void registeRequestParamsHandler(Class<? extends RequestParamsHandler> clazz) {
        RequestParamsHandler handler = handlerMap.get(clazz);
        if (Objects.isNull(handler)) {
            try {
                handler = clazz.getDeclaredConstructor().newInstance();
                handlerMap.put(clazz, handler);
            } catch (Exception e) {
                log.error("创建RequestParamsHandler失败", e);
            }
        }
    }
    /**
     * 缓存RequestParamsHandler
     *
     * @param clazz
     * @return
     */
    protected RequestParamsHandler cacheRequestParamsHandler(
            Class<? extends RequestParamsHandler> clazz) {
        RequestParamsHandler handler = handlerMap.get(clazz);
        if (Objects.isNull(handler)) {
            try {
                handler = clazz.getDeclaredConstructor().newInstance();
                handlerMap.put(clazz, handler);
            } catch (Exception e) {
                log.error("创建RequestParamsHandler失败", e);
                return null;
            }
        }
        return handler;
    }

    /** 避免RequestParamsHandler切换的时候线程安全问题，并且可以传递给子线程 */
    static final class RequestParamsHandlerContext {
        private static final InheritableThreadLocal<RequestParamsHandler>
                RequestParamsHandlerContext = new InheritableThreadLocal<>();

        static void set(RequestParamsHandler requestParamsHandler) {
            clear();
            RequestParamsHandlerContext.set(requestParamsHandler);
        }

        static RequestParamsHandler get() {
            return RequestParamsHandlerContext.get();
        }

        static void clear() {
            RequestParamsHandlerContext.remove();
        }
    }

    /**
     * 设置RequestParamsHandler
     *
     * @param clazz
     * @return
     */
    public AbstractHttp handler(Class<? extends RequestParamsHandler> clazz) {
        RequestParamsHandlerContext.set(cacheRequestParamsHandler(clazz));
        return this;
    }
}
