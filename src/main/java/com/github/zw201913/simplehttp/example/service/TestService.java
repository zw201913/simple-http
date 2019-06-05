package com.github.zw201913.simplehttp.example.service;

import com.github.zw201913.simplehttp.annotation.Field;
import com.github.zw201913.simplehttp.annotation.Header;
import com.github.zw201913.simplehttp.core.handler.RequestParamsHandler;
import com.github.zw201913.simplehttp.core.handler.ResponseHandler;
import com.github.zw201913.simplehttp.example.SimpleHttp;
import lombok.Data;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/** @author zouwei */
@Service
public class TestService {

    @Data
    public static class User {
        /** id */
        private Integer id;
        /** 名字 */
        private String name;
        /** 性别 */
        private Boolean isMan;
        /** 头像名称 */
        private String imageName;
    }

    @Data
    public static class PageParam {
        @Field("name")
        private String search;

        private int page;

        @Header("Content-Type-Ex")
        private String contentType;
    }

    @Data
    public static class RequestHeader {
        @Header("myCookie")
        private String cookie;

        private String userAgent;
        private String host;
        @Field private int size;
    }

    @Autowired private SimpleHttp simpleHttp;

    public String list() {
        return simpleHttp.list("http://127.0.0.1:8090/user/list");
    }

    /**
     * 获取指定id的user
     *
     * @return
     */
    public String find(Integer id) {
        return find();
    }

    public String find() {
        PageParam params = new PageParam();
        params.setSearch("search");
        params.setPage(0);
        // params.setSize(10);
        params.setContentType("text/html");

        RequestHeader headers = new RequestHeader();
        headers.setCookie("zouwei");
        headers.setSize(10);
        headers.setHost("127.0.0.1:8090");
        headers.setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

        return simpleHttp.search("http://127.0.0.1:8090/user/find", params, headers);
    }

    public String add() {
        User user = new User();
        user.setId(2);
        user.setName("zouli");
        user.setIsMan(true);
        File[] files =
                new File[] {
                    new File("/Users/zouwei/Downloads/5452B21E-E360-4768-ACEA-82943649351B.png"),
                    new File("/Users/zouwei/Downloads/5452B21E-E360-4768-ACEA-82943649351B.png")
                };

        simpleHttp.add(
                user,
                files,
                files,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {}

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String result = response.body().string();
                            System.out.println(result);
                        }
                    }
                });
        return "";
    }


    public static class XMLRequestParamsHandler implements RequestParamsHandler {

        @Override
        public RequestBody handle(Map<String, Object> params, Map<String, File[]> files) {
            //实现将params转换成XML格式，并且返回一个RequestBody对象
            return null;
        }
    }

    public static class MyResponseHandler implements ResponseHandler {

        @Override
        public User handle(Response response) {
            //处理Response，将返回数据转换成指定对象
            return new User();
        }
    }
}
