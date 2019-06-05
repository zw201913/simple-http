package com.github.zw201913.simplehttp.support;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.factory.DefaultOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.handler.FormDataJsonRequestParamsHandler;
import com.github.zw201913.simplehttp.core.handler.RequestParamsHandler;
import com.github.zw201913.simplehttp.core.handler.SimpleJsonRequestParamsHandler;
import com.github.zw201913.simplehttp.core.http.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/** @author zouwei */
@Slf4j
public final class HttpUtils {

    private HttpUtils() {}

    static class DefaultSingleOkHttpClient {
        public static final BaseOkHttpClientFactory OK_HTTP_CLIENT_FACTORY = defaultClientFactory();

        /** 用来缓存http */
        private static final Table<BaseOkHttpClientFactory, MethodType, AbstractHttp> HttpTable =
                HashBasedTable.create();

        /**
         * 默认的OkHttpClientFactory，保持单例
         *
         * @return
         */
        private static BaseOkHttpClientFactory defaultClientFactory() {
            return new DefaultOkHttpClientFactory();
        }
        /**
         * 缓存对应的http
         *
         * @param okHttpClientFactory
         * @param methodType
         * @return
         */
        public static AbstractHttp cache(
                BaseOkHttpClientFactory okHttpClientFactory, MethodType methodType) {
            AbstractHttp result = readCache(okHttpClientFactory, methodType);
            if (Objects.isNull(result)) {
                result = newHttp(okHttpClientFactory, methodType);
                if (Objects.isNull(result)) {
                    return result;
                }
                HttpTable.put(okHttpClientFactory, methodType, result);
            }
            return result;
        }

        /**
         * 读取缓存
         *
         * @param okHttpClientFactory
         * @param methodType
         * @return
         */
        public static AbstractHttp readCache(
                BaseOkHttpClientFactory okHttpClientFactory, MethodType methodType) {
            return HttpTable.get(okHttpClientFactory, methodType);
        }

        /**
         * 创建Http对象
         *
         * @param okHttpClientFactory
         * @param methodType
         * @return
         */
        public static AbstractHttp newHttp(
                BaseOkHttpClientFactory okHttpClientFactory, MethodType methodType) {
            if (Objects.equals(methodType, MethodType.GET)) {
                return new GetHttp(okHttpClientFactory);
            } else if (Objects.equals(methodType, MethodType.POST)) {
                return new PostHttp(okHttpClientFactory);
            } else if (Objects.equals(methodType, MethodType.PUT)) {
                return new PutHttp(okHttpClientFactory);
            } else if (Objects.equals(methodType, MethodType.DELETE)) {
                return new DeleteHttp(okHttpClientFactory);
            } else if (Objects.equals(methodType, MethodType.PATCH)) {
                return new PatchHttp(okHttpClientFactory);
            } else if (Objects.equals(methodType, MethodType.HEAD)) {
                return new HeadHttp(okHttpClientFactory);
            } else if (Objects.equals(methodType, MethodType.WS)) {
                return new WebSocketFactory(okHttpClientFactory);
            }
            return null;
        }

        enum MethodType {
            GET,
            POST,
            PUT,
            DELETE,
            PATCH,
            HEAD,
            WS
        }
    }

    /**
     * 注册RequestParamsHandler
     *
     * @param handlerClass
     */
    public static void registeRequestParamsHandler(
            Class<? extends RequestParamsHandler> handlerClass) {
        PostHttp.registeRequestParamsHandler(handlerClass);
    }

    /**
     * 创建默认的WebSocketFactory
     *
     * @return
     */
    public static WebSocketFactory webSocket() {
        return webSocket(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY);
    }

    /**
     * 创建WebSocketFactory
     *
     * @param okHttpClientFactory
     * @return
     */
    public static WebSocketFactory webSocket(BaseOkHttpClientFactory okHttpClientFactory) {
        return (WebSocketFactory)
                DefaultSingleOkHttpClient.cache(
                        okHttpClientFactory, DefaultSingleOkHttpClient.MethodType.WS);
    }
    /**
     * 获取默认的GetHttp
     *
     * @return
     */
    public static GetHttp getHttp() {
        return getHttp(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY);
    }

    /**
     * 获取指定的GetHttp
     *
     * @param okHttpClientFactory
     * @return
     */
    public static GetHttp getHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        return (GetHttp)
                DefaultSingleOkHttpClient.cache(
                        okHttpClientFactory, DefaultSingleOkHttpClient.MethodType.GET);
    }

    /**
     * 获取默认的PostHttp
     *
     * @return
     */
    public static PostHttp postHttp() {
        return postHttp(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY);
    }

    /**
     * 获取指定的PostHttp
     *
     * @param okHttpClientFactory
     * @return
     */
    public static PostHttp postHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        return (PostHttp)
                DefaultSingleOkHttpClient.cache(
                        okHttpClientFactory, DefaultSingleOkHttpClient.MethodType.POST);
    }

    /**
     * 获取默认的PutHttp
     *
     * @return
     */
    public static PutHttp putHttp() {
        return putHttp(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY);
    }

    /**
     * 获取指定的PutHttp
     *
     * @param okHttpClientFactory
     * @return
     */
    public static PutHttp putHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        return (PutHttp)
                DefaultSingleOkHttpClient.cache(
                        okHttpClientFactory, DefaultSingleOkHttpClient.MethodType.PUT);
    }

    /**
     * 获取默认的DeleteHttp
     *
     * @return
     */
    public static DeleteHttp deleteHttp() {
        return deleteHttp(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY);
    }

    /**
     * 获取指定的DeleteHttp
     *
     * @param okHttpClientFactory
     * @return
     */
    public static DeleteHttp deleteHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        return (DeleteHttp)
                DefaultSingleOkHttpClient.cache(
                        okHttpClientFactory, DefaultSingleOkHttpClient.MethodType.DELETE);
    }

    /**
     * 获取默认的PatchHttp
     *
     * @return
     */
    public static PatchHttp patchHttp() {
        return patchHttp(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY);
    }

    /**
     * 获取指定的PatchHttp
     *
     * @param okHttpClientFactory
     * @return
     */
    public static PatchHttp patchHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        return (PatchHttp)
                DefaultSingleOkHttpClient.cache(
                        okHttpClientFactory, DefaultSingleOkHttpClient.MethodType.PATCH);
    }

    /**
     * 获取默认的HeadHttp
     *
     * @return
     */
    public static HeadHttp headHttp() {
        return headHttp(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY);
    }

    /**
     * 获取指定的HeadHttp
     *
     * @param okHttpClientFactory
     * @return
     */
    public static HeadHttp headHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        return (HeadHttp)
                DefaultSingleOkHttpClient.cache(
                        okHttpClientFactory, DefaultSingleOkHttpClient.MethodType.HEAD);
    }

    /**
     * 创建默认的WebSocket
     *
     * @param url
     * @param listener
     * @return
     */
    public static WebSocket newWebSocket(String url, WebSocketListener listener) {
        return newWebSocket(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, listener);
    }
    /**
     * 创建WebSocket
     *
     * @param factory
     * @param url
     * @param listener
     * @return
     */
    public static WebSocket newWebSocket(
            BaseOkHttpClientFactory factory, String url, WebSocketListener listener) {
        return webSocket(factory).createWebSocket(url, listener);
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response get(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return get(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, headers, params);
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static Response get(String url, Map<String, Object> params) {
        return get(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, params);
    }

    /**
     * 发送get请求
     *
     * @param url
     * @return
     */
    public static Response get(String url) {
        return get(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url);
    }

    /**
     * 发送get请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response get(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        try {
            GetHttp getHttp = getHttp(okHttpClientFactory);
            return getHttp.send(url, headers, params);
        } catch (IOException e) {
            log.error("请求失败", e);
            return null;
        }
    }

    /**
     * 发送get请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response get(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return get(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送get请求
     *
     * @param okHttpClientFactory
     * @param url
     * @return
     */
    public static Response get(BaseOkHttpClientFactory okHttpClientFactory, String url) {
        return get(okHttpClientFactory, url, null);
    }

    /**
     * 异步get请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param responseCallback
     */
    public static void getAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        getHttp(okHttpClientFactory).sendAsync(url, headers, params, responseCallback);
    }

    /**
     * 异步get请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param responseCallback
     */
    public static void getAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        getAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 异步get请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param responseCallback
     */
    public static void getAsync(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Callback responseCallback) {
        getAsync(okHttpClientFactory, url, null, responseCallback);
    }

    /**
     * 异步get请求
     *
     * @param url
     * @param headers
     * @param params
     * @param responseCallback
     */
    public static void getAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        getAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                responseCallback);
    }

    /**
     * 异步get请求
     *
     * @param url
     * @param params
     * @param responseCallback
     */
    public static void getAsync(String url, Map<String, Object> params, Callback responseCallback) {
        getAsync(url, null, params, responseCallback);
    }

    /**
     * 异步get请求
     *
     * @param url
     * @param responseCallback
     */
    public static void getAsync(String url, Callback responseCallback) {
        getAsync(url, null, responseCallback);
    }

    /**
     * 发送head请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response head(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return head(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, headers, params);
    }

    /**
     * 发送head请求
     *
     * @param url
     * @param params
     * @return
     */
    public static Response head(String url, Map<String, Object> params) {
        return head(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, params);
    }

    /**
     * 发送head请求
     *
     * @param url
     * @return
     */
    public static Response head(String url) {
        return head(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url);
    }

    /**
     * 发送head请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response head(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        try {
            return headHttp(okHttpClientFactory).send(url, headers, params);
        } catch (IOException e) {
            log.error("请求失败", e);
            return null;
        }
    }

    /**
     * 发送head请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response head(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return head(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送head请求
     *
     * @param okHttpClientFactory
     * @param url
     * @return
     */
    public static Response head(BaseOkHttpClientFactory okHttpClientFactory, String url) {
        return head(okHttpClientFactory, url, null);
    }

    /**
     * 异步head请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param responseCallback
     */
    public static void headAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        headHttp(okHttpClientFactory).sendAsync(url, headers, params, responseCallback);
    }

    /**
     * 异步head请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param responseCallback
     */
    public static void headAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        headAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 异步head请求
     *
     * @param okHttpClientFactory
     * @param url
     * @param responseCallback
     */
    public static void headAsync(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Callback responseCallback) {
        headAsync(okHttpClientFactory, url, null, responseCallback);
    }

    /**
     * 异步head请求
     *
     * @param url
     * @param headers
     * @param params
     * @param responseCallback
     */
    public static void headAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        headAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                responseCallback);
    }

    /**
     * 异步head请求
     *
     * @param url
     * @param params
     * @param responseCallback
     */
    public static void headAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        headAsync(url, null, params, responseCallback);
    }

    /**
     * 异步head请求
     *
     * @param url
     * @param responseCallback
     */
    public static void headAsync(String url, Callback responseCallback) {
        headAsync(url, null, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response postFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        try {
            return postHttp(okHttpClientFactory)
                    .handler(FormDataJsonRequestParamsHandler.class)
                    .send(url, headers, params, files, progressListener);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response postFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return postFiles(okHttpClientFactory, url, null, params, files, progressListener);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response postFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return postFiles(okHttpClientFactory, url, null, files, progressListener);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response postFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return postFiles(okHttpClientFactory, url, headers, params, files, null);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response postFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return postFiles(okHttpClientFactory, url, null, params, files);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static Response postFiles(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, File[]> files) {
        return postFiles(okHttpClientFactory, url, null, files);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response postFormData(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        return postFiles(okHttpClientFactory, url, headers, params, null);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response postFormData(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return postFormData(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response postFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return postFiles(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response postFiles(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return postFiles(url, null, params, files, progressListener);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response postFiles(
            String url, Map<String, File[]> files, ProgressListener progressListener) {
        return postFiles(url, null, files, progressListener);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response postFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return postFiles(url, headers, params, files, null);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response postFiles(
            String url, Map<String, Object> params, Map<String, File[]> files) {
        return postFiles(url, null, params, files);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static Response postFiles(String url, Map<String, File[]> files) {
        return postFiles(url, null, files);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response postFormData(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return postFiles(url, headers, params, null);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response postFormData(String url, Map<String, Object> params) {
        return postFormData(url, null, params);
    }

    /**
     * 发送post异步请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void postFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        postHttp(okHttpClientFactory)
                .handler(FormDataJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, files, progressListener, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void postFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        postFilesAsync(
                okHttpClientFactory, url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void postFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        postFilesAsync(okHttpClientFactory, url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void postFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        postFilesAsync(okHttpClientFactory, url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void postFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        postFilesAsync(okHttpClientFactory, url, null, params, files, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static void postFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            Callback responseCallback) {
        postFilesAsync(okHttpClientFactory, url, null, files, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void postFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        postFilesAsync(okHttpClientFactory, url, headers, params, null, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void postFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        postFormDataAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void postFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        postFilesAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener,
                responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void postFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        postFilesAsync(url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void postFilesAsync(
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        postFilesAsync(url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void postFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        postFilesAsync(url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void postFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        postFilesAsync(url, null, params, files, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static void postFilesAsync(
            String url, Map<String, File[]> files, Callback responseCallback) {
        postFilesAsync(url, null, files, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void postFormDataAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        postFilesAsync(url, headers, params, null, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void postFormDataAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        postFormDataAsync(url, null, params, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response post(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        try {
            return postHttp(okHttpClientFactory)
                    .handler(SimpleJsonRequestParamsHandler.class)
                    .send(url, headers, params);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response post(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return post(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response post(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return post(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, headers, params);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static Response post(String url, Map<String, Object> params) {
        return post(url, null, params);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void postAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        postHttp(okHttpClientFactory)
                .handler(SimpleJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void postAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        postAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void postAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        postAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                responseCallback);
    }

    /**
     * 发送post请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static void postAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        postAsync(url, null, params, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response putFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        try {
            return putHttp(okHttpClientFactory)
                    .handler(FormDataJsonRequestParamsHandler.class)
                    .send(url, headers, params, files, progressListener);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response putFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return putFiles(okHttpClientFactory, url, null, params, files, progressListener);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response putFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return putFiles(okHttpClientFactory, url, null, files, progressListener);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response putFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return putFiles(okHttpClientFactory, url, headers, params, files, null);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response putFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return putFiles(okHttpClientFactory, url, null, params, files);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static Response putFiles(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, File[]> files) {
        return putFiles(okHttpClientFactory, url, null, files);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response putFormData(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        return putFiles(okHttpClientFactory, url, headers, params, null);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response putFormData(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return putFormData(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response putFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return putFiles(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response putFiles(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return putFiles(url, null, params, files, progressListener);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response putFiles(
            String url, Map<String, File[]> files, ProgressListener progressListener) {
        return putFiles(url, null, files, progressListener);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response putFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return putFiles(url, headers, params, files, null);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response putFiles(
            String url, Map<String, Object> params, Map<String, File[]> files) {
        return putFiles(url, null, params, files);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static Response putFiles(String url, Map<String, File[]> files) {
        return putFiles(url, null, files);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response putFormData(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return putFiles(url, headers, params, null);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response putFormData(String url, Map<String, Object> params) {
        return putFormData(url, null, params);
    }

    /**
     * 发送put异步请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void putFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        putHttp(okHttpClientFactory)
                .handler(FormDataJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, files, progressListener, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void putFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        putFilesAsync(
                okHttpClientFactory, url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void putFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        putFilesAsync(okHttpClientFactory, url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void putFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        putFilesAsync(okHttpClientFactory, url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void putFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        putFilesAsync(okHttpClientFactory, url, null, params, files, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static void putFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            Callback responseCallback) {
        putFilesAsync(okHttpClientFactory, url, null, files, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void putFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        putFilesAsync(okHttpClientFactory, url, headers, params, null, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void putFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        putFormDataAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void putFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        putFilesAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener,
                responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void putFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        putFilesAsync(url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void putFilesAsync(
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        putFilesAsync(url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void putFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        putFilesAsync(url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void putFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        putFilesAsync(url, null, params, files, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static void putFilesAsync(
            String url, Map<String, File[]> files, Callback responseCallback) {
        putFilesAsync(url, null, files, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void putFormDataAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        putFilesAsync(url, headers, params, null, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void putFormDataAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        putFormDataAsync(url, null, params, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response put(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        try {
            return putHttp(okHttpClientFactory)
                    .handler(SimpleJsonRequestParamsHandler.class)
                    .send(url, headers, params);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response put(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return put(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response put(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return put(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, headers, params);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static Response put(String url, Map<String, Object> params) {
        return put(url, null, params);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void putAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        putHttp(okHttpClientFactory)
                .handler(SimpleJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void putAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        putAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void putAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        putAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                responseCallback);
    }

    /**
     * 发送put请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static void putAsync(String url, Map<String, Object> params, Callback responseCallback) {
        putAsync(url, null, params, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response patchFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        try {
            return patchHttp(okHttpClientFactory)
                    .handler(FormDataJsonRequestParamsHandler.class)
                    .send(url, headers, params, files, progressListener);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response patchFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return patchFiles(okHttpClientFactory, url, null, params, files, progressListener);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response patchFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return patchFiles(okHttpClientFactory, url, null, files, progressListener);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response patchFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return patchFiles(okHttpClientFactory, url, headers, params, files, null);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response patchFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return patchFiles(okHttpClientFactory, url, null, params, files);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static Response patchFiles(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, File[]> files) {
        return patchFiles(okHttpClientFactory, url, null, files);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response patchFormData(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        return patchFiles(okHttpClientFactory, url, headers, params, null);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response patchFormData(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return patchFormData(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response patchFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return patchFiles(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response patchFiles(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return patchFiles(url, null, params, files, progressListener);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response patchFiles(
            String url, Map<String, File[]> files, ProgressListener progressListener) {
        return patchFiles(url, null, files, progressListener);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response patchFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return patchFiles(url, headers, params, files, null);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response patchFiles(
            String url, Map<String, Object> params, Map<String, File[]> files) {
        return patchFiles(url, null, params, files);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static Response patchFiles(String url, Map<String, File[]> files) {
        return patchFiles(url, null, files);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response patchFormData(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return patchFiles(url, headers, params, null);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response patchFormData(String url, Map<String, Object> params) {
        return patchFormData(url, null, params);
    }

    /**
     * 发送patch异步请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void patchFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        patchHttp(okHttpClientFactory)
                .handler(FormDataJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, files, progressListener, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void patchFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        patchFilesAsync(
                okHttpClientFactory, url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void patchFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        patchFilesAsync(okHttpClientFactory, url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void patchFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        patchFilesAsync(okHttpClientFactory, url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void patchFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        patchFilesAsync(okHttpClientFactory, url, null, params, files, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static void patchFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            Callback responseCallback) {
        patchFilesAsync(okHttpClientFactory, url, null, files, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void patchFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        patchFilesAsync(okHttpClientFactory, url, headers, params, null, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void patchFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        patchFormDataAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void patchFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        patchFilesAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener,
                responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void patchFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        patchFilesAsync(url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void patchFilesAsync(
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        patchFilesAsync(url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void patchFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        patchFilesAsync(url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void patchFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        patchFilesAsync(url, null, params, files, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static void patchFilesAsync(
            String url, Map<String, File[]> files, Callback responseCallback) {
        patchFilesAsync(url, null, files, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void patchFormDataAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        patchFilesAsync(url, headers, params, null, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void patchFormDataAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        patchFormDataAsync(url, null, params, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response patch(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        try {
            return patchHttp(okHttpClientFactory)
                    .handler(SimpleJsonRequestParamsHandler.class)
                    .send(url, headers, params);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response patch(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return patch(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response patch(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return patch(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, headers, params);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static Response patch(String url, Map<String, Object> params) {
        return patch(url, null, params);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void patchAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        patchHttp(okHttpClientFactory)
                .handler(SimpleJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void patchAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        patchAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void patchAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        patchAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                responseCallback);
    }

    /**
     * 发送patch请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static void patchAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        patchAsync(url, null, params, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response deleteFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        try {
            return deleteHttp(okHttpClientFactory)
                    .handler(FormDataJsonRequestParamsHandler.class)
                    .send(url, headers, params, files, progressListener);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * @param okHttpClientFactory
     * @param url
     * @return
     */
    public static Response delete(BaseOkHttpClientFactory okHttpClientFactory, String url) {
        try {
            return deleteHttp(okHttpClientFactory).send(url);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * @param url
     * @return
     */
    public static Response delete(String url) {
        return delete(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url);
    }

    /**
     * @param okHttpClientFactory
     * @param url
     * @return
     */
    public static void deleteAsync(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Callback responseCallback) {
        deleteHttp(okHttpClientFactory).sendAsync(url, responseCallback);
    }

    /**
     * @param url
     * @return
     */
    public static void delete(String url, Callback responseCallback) {
        deleteAsync(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response deleteFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return deleteFiles(okHttpClientFactory, url, null, params, files, progressListener);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response deleteFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return deleteFiles(okHttpClientFactory, url, null, files, progressListener);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response deleteFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return deleteFiles(okHttpClientFactory, url, headers, params, files, null);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response deleteFiles(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return deleteFiles(okHttpClientFactory, url, null, params, files);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static Response deleteFiles(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, File[]> files) {
        return deleteFiles(okHttpClientFactory, url, null, files);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response deleteFormData(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        return deleteFiles(okHttpClientFactory, url, headers, params, null);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response deleteFormData(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return deleteFormData(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response deleteFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return deleteFiles(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static Response deleteFiles(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        return deleteFiles(url, null, params, files, progressListener);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static Response deleteFiles(
            String url, Map<String, File[]> files, ProgressListener progressListener) {
        return deleteFiles(url, null, files, progressListener);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static Response deleteFiles(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files) {
        return deleteFiles(url, headers, params, files, null);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static Response deleteFiles(
            String url, Map<String, Object> params, Map<String, File[]> files) {
        return deleteFiles(url, null, params, files);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static Response deleteFiles(String url, Map<String, File[]> files) {
        return deleteFiles(url, null, files);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response deleteFormData(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return deleteFiles(url, headers, params, null);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static Response deleteFormData(String url, Map<String, Object> params) {
        return deleteFormData(url, null, params);
    }

    /**
     * 发送delete异步请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void deleteFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        deleteHttp(okHttpClientFactory)
                .handler(FormDataJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, files, progressListener, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void deleteFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        deleteFilesAsync(
                okHttpClientFactory, url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void deleteFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        deleteFilesAsync(okHttpClientFactory, url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void deleteFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        deleteFilesAsync(okHttpClientFactory, url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void deleteFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        deleteFilesAsync(okHttpClientFactory, url, null, params, files, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param files
     * @return
     */
    public static void deleteFilesAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, File[]> files,
            Callback responseCallback) {
        deleteFilesAsync(okHttpClientFactory, url, null, files, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void deleteFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        deleteFilesAsync(okHttpClientFactory, url, headers, params, null, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void deleteFormDataAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        deleteFormDataAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void deleteFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        deleteFilesAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                files,
                progressListener,
                responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    public static void deleteFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        deleteFilesAsync(url, null, params, files, progressListener, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     */
    public static void deleteFilesAsync(
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        deleteFilesAsync(url, null, files, progressListener, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     */
    public static void deleteFilesAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        deleteFilesAsync(url, headers, params, files, null, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static void deleteFilesAsync(
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback responseCallback) {
        deleteFilesAsync(url, null, params, files, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param files
     * @return
     */
    public static void deleteFilesAsync(
            String url, Map<String, File[]> files, Callback responseCallback) {
        deleteFilesAsync(url, null, files, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void deleteFormDataAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        deleteFilesAsync(url, headers, params, null, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestPart
     *
     * @param url
     * @param params
     * @return
     */
    public static void deleteFormDataAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        deleteFormDataAsync(url, null, params, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response delete(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params) {
        try {
            return deleteHttp(okHttpClientFactory)
                    .handler(SimpleJsonRequestParamsHandler.class)
                    .send(url, headers, params);
        } catch (IOException e) {
            log.error("请求发送失败", e);
            return null;
        }
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static Response delete(
            BaseOkHttpClientFactory okHttpClientFactory, String url, Map<String, Object> params) {
        return delete(okHttpClientFactory, url, null, params);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static Response delete(
            String url, Map<String, String> headers, Map<String, Object> params) {
        return delete(DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY, url, headers, params);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static Response delete(String url, Map<String, Object> params) {
        return delete(url, null, params);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void deleteAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        deleteHttp(okHttpClientFactory)
                .handler(SimpleJsonRequestParamsHandler.class)
                .sendAsync(url, headers, params, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param okHttpClientFactory
     * @param url
     * @param params
     * @return
     */
    public static void deleteAsync(
            BaseOkHttpClientFactory okHttpClientFactory,
            String url,
            Map<String, Object> params,
            Callback responseCallback) {
        deleteAsync(okHttpClientFactory, url, null, params, responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static void deleteAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        deleteAsync(
                DefaultSingleOkHttpClient.OK_HTTP_CLIENT_FACTORY,
                url,
                headers,
                params,
                responseCallback);
    }

    /**
     * 发送delete请求，对应spring mvc中的@RequestBody
     *
     * @param url
     * @param params
     * @return
     */
    public static void deleteAsync(
            String url, Map<String, Object> params, Callback responseCallback) {
        deleteAsync(url, null, params, responseCallback);
    }
}
