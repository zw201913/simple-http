package com.github.zw201913.simplehttp.core.http;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.handler.RequestParamsHandler;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/** @author zouwei */
public class DeleteHttp extends PostHttp {

    /**
     * 构造函数
     *
     * @param okHttpClientFactory
     */
    public DeleteHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        super(okHttpClientFactory);
    }
    /**
     * 构造函数
     *
     * @param okHttpClientFactory
     * @param clazz
     */
    public DeleteHttp(
            BaseOkHttpClientFactory okHttpClientFactory,
            Class<? extends RequestParamsHandler> clazz) {
        super(okHttpClientFactory, clazz);
    }

    @Override
    protected void enhanceRequestBuilder(
            Request.Builder builder,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        RequestBody requestBody = handleRequestBody(params, files, progressListener);
        if (Objects.isNull(requestBody)) {
            builder.delete();
        } else {
            builder.delete(requestBody);
        }
    }

    /**
     * @param url
     * @return
     * @throws IOException
     */
    public Response execute(String url) throws IOException {
        return super.send(url, null);
    }

    /**
     * @param url
     * @param responseCallback
     * @return
     * @throws IOException
     */
    public void executeAsync(String url, Callback responseCallback) {
        super.sendAsync(url, responseCallback);
    }
}
