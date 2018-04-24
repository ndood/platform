package com.fulu.game.play.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {


    @GetMapping
    @RequestMapping(value = "/")
    @ResponseBody
    public String index(){

        return "hello";
    }
}
