package me.nixuehan.demo.redpacket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/red")
public class Redpacket {

    @ResponseBody
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
