package com.fulu.game.play.controller.exception;

import com.fulu.game.common.Result;
import com.fulu.game.common.exception.ServiceErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {


    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg("数据库中已存在该记录");
    }

    @ExceptionHandler(DataAccessException.class)
    public Result handleDataAccessException(DataAccessException e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg("SQL执行错误:"+e.getCause().getMessage());
    }


    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public Result noHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg("没找找到页面");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg("必填参数空:"+e.getMessage());
    }

    @ExceptionHandler(ServiceErrorException.class)
    public Result  serviceErrorException(ServiceErrorException e){
        log.error(e.getMessage(), e);
        return	Result.error().msg(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg("服务器错误");
    }
}
