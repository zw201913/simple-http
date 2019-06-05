package com.github.zw201913.simplehttp.core.handler;

import okhttp3.Response;

/** @author zouwei */
public interface ResponseHandler {
    /**
     * 处理响应对象
     *
     * @param response
     * @param <T>
     * @return
     */
    <T> T handle(Response response);
}
