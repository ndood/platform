package com.fulu.game.core.entity;

import lombok.Data;

@Data
public class ImUser {
    private String uuid;
    private String type;
    private String created;
    private String modified;
    private String username;
    private Boolean activated;
}
