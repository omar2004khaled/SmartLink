package com.example.auth.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "You accessed protected data";
    }
}