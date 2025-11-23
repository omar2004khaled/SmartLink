package com.example.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "You accessed protected data";
    }

    @GetMapping("/hello")
    public String test() {
        return "Backend connected successfully ✅";
    }

}