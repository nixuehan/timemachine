package me.nixuehan.demo.capital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class Capital {

    @ResponseBody
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
