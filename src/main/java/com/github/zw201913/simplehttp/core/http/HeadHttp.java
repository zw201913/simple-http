package com.github.zw201913.simplehttp.core.http;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import okhttp3.Request;

import java.io.File;
import java.util.Map;

/** @author zouwei */
public class HeadHttp extends GetHttp {
    /**
     * 构造函数
     *
     * @param okHttpClientFactory
     */
    public HeadHttp(BaseOkHttpClientFactory okHttpClientFactory) {
        super(okHttpClientFactory);
    }

    @Override
    protected void enhanceRequestBuilder(
            Request.Builder builder,
            String url,
            Map<String, Object> params,
            Map<String, File[]> files,
            ProgressListener progressListener) {
        builder.head();
    }
}
