package com.github.zw201913.simplehttp.demo.service;

import com.github.zw201913.simplehttp.demo.SimpleHttp;
import okhttp3.*;
import okio.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

/** @author zouwei */
@Service
public class TestService {

    @Autowired private SimpleHttp simpleHttp;

    public String test() {
        simpleHttp.test();
        return "";
        //        String result = response.body().string();
        //        response.close();
        //        return result;
        // return null;
    }

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("").build();
        WebSocket webSocket =
                client.newWebSocket(
                        request,
                        new WebSocketListener() {
                            @Override
                            public void onOpen(WebSocket webSocket, Response response) {
                                super.onOpen(webSocket, response);
                            }

                            @Override
                            public void onMessage(WebSocket webSocket, String text) {
                                super.onMessage(webSocket, text);
                            }

                            @Override
                            public void onMessage(WebSocket webSocket, ByteString bytes) {
                                super.onMessage(webSocket, bytes);
                            }

                            @Override
                            public void onClosing(WebSocket webSocket, int code, String reason) {
                                super.onClosing(webSocket, code, reason);
                            }

                            @Override
                            public void onClosed(WebSocket webSocket, int code, String reason) {
                                super.onClosed(webSocket, code, reason);
                            }

                            @Override
                            public void onFailure(
                                    WebSocket webSocket, Throwable t, @Nullable Response response) {
                                super.onFailure(webSocket, t, response);
                            }
                        });

        // webSocket.
    }
}
