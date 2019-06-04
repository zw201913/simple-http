package com.github.zw201913.simplehttp.core.handler;

import okhttp3.RequestBody;

import java.io.File;
import java.util.Map;

/** @author zouwei */
public interface RequestParamsHandler {

    String JSON_UTF8 = "application/json;charset=utf-8";

    /**
     * 处理请求参数
     *
     * @param params
     * @param files
     */
    RequestBody handle(Map<String, Object> params, Map<String, File[]> files);
}
