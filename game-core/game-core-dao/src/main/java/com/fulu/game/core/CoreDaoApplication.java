package com.fulu.game.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by burgl on 2018/4/14.
 */
@SpringBootApplication
@ComponentScan("com.fulu.game")
public class CoreDaoApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreDaoApplication.class, args);
    }

}
