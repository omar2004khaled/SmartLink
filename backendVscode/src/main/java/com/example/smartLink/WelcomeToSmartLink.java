package com.example.smartLink;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeToSmartLink {

    @GetMapping("/")
    public String hello() {
        return "omarkhaled!";
    }
}