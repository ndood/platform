package com.fulu.game.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by bwang.abcft on 2018/4/13.
 */
@SpringBootApplication
@ComponentScan("com.fulu.game")
public class AdminApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(AdminApplication.class, args);
    }



}
