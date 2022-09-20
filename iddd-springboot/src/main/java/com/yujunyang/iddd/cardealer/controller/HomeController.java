package com.yujunyang.iddd.cardealer.controller;

import java.text.MessageFormat;

import com.yujunyang.iddd.cardealer.infrastructure.aop.AuthenticationContextDataInject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping("")
    public String sayHello() {
        return "hello";
    }

    @GetMapping("inject")
    public String inject(@AuthenticationContextDataInject("user?.userId") Integer userId) {
        return MessageFormat.format("userId:{0}", userId);
    }
}
