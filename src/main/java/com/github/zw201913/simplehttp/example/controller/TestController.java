package com.github.zw201913.simplehttp.example.controller;

import com.github.zw201913.simplehttp.example.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired private TestService testService;

    @GetMapping("/list")
    public String list() {
        return testService.list();
    }

    @GetMapping("/find/{id}")
    public String find(@PathVariable("id") Integer id) {
        return testService.find(id);
    }

    @GetMapping("/add")
    public String add() {
        return testService.add();
    }
}
