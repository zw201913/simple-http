package com.github.zw201913.simplehttp.core.proxy;

import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.handler.RequestParamsHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpMethodType {

    private final String url;

    private final BaseOkHttpClientFactory okHttpClientFactory;

    private final Class<? extends RequestParamsHandler> handlerClass;

    private final MethodType methodType;

    enum MethodType {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        PATCH,
        WS
    }
}
