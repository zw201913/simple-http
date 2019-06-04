package com.github.zw201913.simplehttp.core.factory;

import okhttp3.OkHttpClient;

/** @author zouwei */
public abstract class BaseOkHttpClientFactory {

    /** final是为了保持单例 */
    private final OkHttpClient client;

    public BaseOkHttpClientFactory() {
        this.client = httpClient();
    }

    /**
     * 留给子类实现
     *
     * @return
     */
    protected abstract OkHttpClient httpClient();

    /**
     * 获取创建好的OkHttpClient
     *
     * @return
     */
    public OkHttpClient okHttpClient() {
        return this.client;
    }
}
