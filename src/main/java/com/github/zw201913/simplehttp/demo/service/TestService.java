package com.github.zw201913.simplehttp.demo.service;

import com.github.zw201913.simplehttp.demo.SimpleHttp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
