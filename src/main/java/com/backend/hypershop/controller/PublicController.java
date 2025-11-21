package com.backend.hypershop.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public")
public class PublicController {

    @GetMapping
    public String status(){
        return "Server is running";
    }

    @GetMapping("test")
    public String test(){
        return "THis is ci cd test";
    }
}
