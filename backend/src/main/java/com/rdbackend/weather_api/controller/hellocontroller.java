package com.rdbackend.weather_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hellocontroller {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

}
