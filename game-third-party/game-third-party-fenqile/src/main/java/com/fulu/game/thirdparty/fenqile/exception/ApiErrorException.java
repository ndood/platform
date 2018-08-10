package com.fulu.game.thirdparty.fenqile.exception;

import lombok.Getter;

@Getter
public class ApiErrorException extends RuntimeException{

    public String message;

    public ApiErrorException(String message) {
        super();
        this.message = message;
    }
}
