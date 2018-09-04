package com.fulu.game.core.service.queue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Msg implements Serializable{

    public Msg(){};


    private String id;

    private String content;

}
