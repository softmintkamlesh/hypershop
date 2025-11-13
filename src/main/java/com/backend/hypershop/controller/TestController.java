package com.backend.hypershop.controller;


import com.backend.hypershop.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

        @GetMapping("hello")
        public  String hello(){
            return testService.hello();
        }
}
