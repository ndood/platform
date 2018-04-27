package com.fulu.game.admin.controller.exception;

import com.fulu.game.common.Result;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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


    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg("存在关联数据，无法删除!");
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

    @ExceptionHandler(CashException.class)
    public Result cashException(CashException e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg(e.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public Result UserException(UserException e) {
        log.error(e.getMessage(), e);
        return	Result.error().msg(e.getMessage());
    }
}
