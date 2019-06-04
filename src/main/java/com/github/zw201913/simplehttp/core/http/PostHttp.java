package com.github.zw201913.simplehttp.core.http;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.handler.RequestParamsHandler;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/** @author zouwei */
@Slf4j
public class PostHttp extends AbstractHttp {

    @Override
    protected void enhanceRequestBuilder(
            Request.Builder builder,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        RequestBody requestBody = handleRequestBody(params, files, progressListener);
        if (Objects.isNull(requestBody)) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        builder.post(requestBody);
    }

    /**
     * 处理请求数据并创建RequestBody
     *
     * @param params
     * @param files
     * @param progressListener
     * @return
     */
    protected RequestBody handleRequestBody(
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        RequestBody requestBody = RequestParamsHandlerContext.get().handle(params, files);
        if (!Objects.isNull(progressListener)
                && !Objects.isNull(requestBody)
                && requestBody instanceof MultipartBody) {
            return new ProgressRequestBody(((MultipartBody) requestBody), progressListener);
        }
        return requestBody;
    }

    /**
     * 构造函数
     *
     * @param okHttpClientFactory
     */
    public PostHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        super(okHttpClientFactory);
    }
    /**
     * 构造函数
     *
     * @param okHttpClientFactory
     * @param clazz
     */
    public PostHttp(
            BaseOkHttpClientFactory okHttpClientFactory,
            Class<? extends RequestParamsHandler> clazz) {
        super(okHttpClientFactory);
        RequestParamsHandlerContext.set(cacheRequestParamsHandler(clazz));
    }



    /**
     * 发送post请求(有文件的话，一定要使用支持文件上传的RequestParamsHandler)
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @return
     * @throws IOException
     */
    public Response execute(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener)
            throws IOException {
        return send(url, headers, params, files, progressListener);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param files
     * @param progressListener
     * @return
     * @throws IOException
     */
    public Response execute(
            String url, Map<String, File[]> files, ProgressListener progressListener)
            throws IOException {
        return send(url, null, null, files, progressListener);
    }

    /**
     * 发送post异步请求
     *
     * @param url
     * @param headers
     * @param params
     * @param files
     * @param responseCallback
     * @return
     */
    public void executeAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        sendAsync(url, headers, params, files, progressListener, responseCallback);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param files
     * @param progressListener
     * @param responseCallback
     */
    public void executeAsync(
            String url,
            Map<String, File[]> files,
            ProgressListener progressListener,
            Callback responseCallback) {
        sendAsync(url, null, null, files, progressListener, responseCallback);
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     * @throws IOException
     */
    public Response execute(String url, Map<String, String> headers, Map<String, Object> params)
            throws IOException {
        return execute(url, headers, params, null, null);
    }

    /**
     * 发送post异步请求
     *
     * @param url
     * @param headers
     * @param params
     * @param responseCallback
     */
    public void executeAsync(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Callback responseCallback) {
        executeAsync(url, headers, params, null, null, responseCallback);
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public Response execute(String url, Map<String, Object> params) throws IOException {
        return execute(url, null, params);
    }

    /**
     * 发送post异步请求
     *
     * @param url
     * @param params
     * @param responseCallback
     */
    public void executeAsync(String url, Map<String, Object> params, Callback responseCallback) {
        executeAsync(url, null, params, responseCallback);
    }
}
