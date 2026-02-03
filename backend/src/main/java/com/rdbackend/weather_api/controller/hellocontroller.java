package com.rdbackend.weather_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hellocontroller {
    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return "Hello " + name;
    }

}
