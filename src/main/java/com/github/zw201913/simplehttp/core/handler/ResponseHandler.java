package com.github.zw201913.simplehttp.core.handler;

import okhttp3.Response;

public interface ResponseHandler<T> {

    <T> T handle(Response response);
}
