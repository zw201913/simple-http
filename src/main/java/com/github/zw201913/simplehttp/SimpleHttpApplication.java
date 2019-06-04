package com.github.zw201913.simplehttp;

import com.github.zw201913.simplehttp.annotation.EnableSimpleHttp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableSimpleHttp
@SpringBootApplication
public class SimpleHttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleHttpApplication.class, args);
    }
}
