package com.github.zw201913.simplehttp.demo;

import com.github.zw201913.simplehttp.annotation.Get;
import com.github.zw201913.simplehttp.annotation.SimpleHttpService;

@SimpleHttpService
public interface SimpleHttp {

    @Get("http://www.baidu.com")
    String getBaidu();

    @Get("https://www.cnblogs.com/lnlvinso/p/10898109.html")
    void test();




}
