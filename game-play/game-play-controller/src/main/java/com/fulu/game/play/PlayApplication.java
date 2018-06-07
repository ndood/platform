package com.fulu.game.play;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by bwang.abcft on 2018/4/13.
 */
@SpringBootApplication
@ComponentScan("com.fulu.game")
public class PlayApplication extends SpringBootServletInitializer {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(PlayApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PlayApplication.class);
    }



}
