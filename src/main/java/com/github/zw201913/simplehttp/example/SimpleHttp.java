package com.github.zw201913.simplehttp.example;

import com.github.zw201913.simplehttp.annotation.*;
import com.github.zw201913.simplehttp.example.service.TestService;
import okhttp3.Callback;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.File;
import java.util.Map;

@SimpleHttpService
public interface SimpleHttp {

    /**
     * @GetMapping("/list") @ResponseBody public Collection<User> users() { }
     *
     * <p>发送get请求
     *
     * @return
     */
    @Get("http://127.0.0.1:8090/user/list")
    String list();

    /**
     * url未定的get请求，更灵活
     *
     * @param url
     * @return
     */
    @Get
    String list(@Url String url);

    /**
     * @GetMapping("/find") @ResponseBody public User find(Integer id)
     *
     * @param id
     * @return
     */
    @Get("http://127.0.0.1:8090/user/find")
    String find(@Field("id") Integer id, @Header("name") String name);

    @Get
    String find(
            @Url String url,
            @Field Map<String, Object> params,
            @Header Map<String, String> headers);

    @Get
    String search(
            @Url String url,
            @Field TestService.PageParam params,
            @Header TestService.RequestHeader headers);

    @Post("http://127.0.0.1:8090/user/addWithImage")
    String add(@Field("user") Map<String, Object> user, @Field("image") File file);

    @Post(
            value = "http://127.0.0.1:8090/user/addWithImage",
            handler = TestService.XMLRequestParamsHandler.class)
    void add(
            @Field("user") TestService.User user,
            @Field("image1") File[] file1,
            @Field("image2") File[] file2,
            Callback callback);

    @Ws
    WebSocket newWebSocket(@Url String url, WebSocketListener listener);

    @Ws("ws://echo.websocket.org")
    WebSocket newWebSocket(WebSocketListener listener);
}
