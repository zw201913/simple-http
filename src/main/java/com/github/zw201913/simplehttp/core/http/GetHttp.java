package com.github.zw201913.simplehttp.core.http;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.StringJoiner;

/** @author zouwei */
public class GetHttp extends AbstractHttp {

    /**
     * 构造函数
     *
     * @param okHttpClientFactory
     */
    public GetHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        super(okHttpClientFactory);
    }

    /**
     * GET请求直接拼接参数，不支持文件上传
     *
     * @param builder
     * @param url
     * @param params
     * @param files
     */
    @Override
    protected void enhanceRequestBuilder(
            Request.Builder builder,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        builder.get();
    }

    /**
     * 修改url
     *
     * @param builder
     * @param url
     * @param params
     */
    @Override
    protected void enhanceUrl(Request.Builder builder, String url, Map<String, Object> params) {
        builder.url(joinUrlAndParams(url, params));
    }

    /**
     * 拼接url和参数
     *
     * @param url
     * @param params
     * @return
     */
    private String joinUrlAndParams(String url, Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            return url;
        }
        StringJoiner joiner = new StringJoiner("&");
        params.forEach((key, value) -> joiner.add(key + "=" + value));
        return url + "?" + joiner.toString();
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     * @throws IOException
     */
    public Response execute(String url, Map<String, String> headers, Map<String, Object> params)
            throws IOException {
        return send(url, headers, params, null, null);
    }

    /**
     * 发送get异步请求
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
        sendAsync(url, headers, params, responseCallback);
    }

    /**
     * 发送get请求
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
     * 发送get异步请求
     *
     * @param url
     * @param params
     * @param responseCallback
     */
    public void executeAsync(String url, Map<String, Object> params, Callback responseCallback) {
        executeAsync(url, null, params, responseCallback);
    }
    /**
     * 发送get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Response execute(String url) throws IOException {
        return execute(url, null);
    }

    /**
     * 发送get异步请求
     *
     * @param url
     * @param responseCallback
     */
    public void executeAsync(String url, Callback responseCallback) {
        executeAsync(url, null, responseCallback);
    }
}
