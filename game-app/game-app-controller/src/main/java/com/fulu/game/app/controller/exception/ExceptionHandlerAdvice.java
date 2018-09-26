package com.fulu.game.app.controller.exception;

import com.fulu.game.common.Result;
import com.fulu.game.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {

    /**
     * 判断唯一索引
     *
     * @param e
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("SQL异常", e);
        return Result.error().msg("服务器错误!");
    }

    /**
     * SQL执行错误
     * @param e
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    public Result handleDataAccessException(DataAccessException e) {
        log.error("SQL执行错误:", e);
        return Result.error().msg("服务器错误!");
    }

    /**
     * 404错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public Result noHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("未找到对应页面");
    }

    /**
     * 必填参数为空
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("必填参数空:" + e.getParameterName());
    }

    /**
     * 业务错误,直接提示给用户
     * @param e
     * @return
     */
    @ExceptionHandler(ServiceErrorException.class)
    public Result serviceErrorException(ServiceErrorException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg(e.getMessage());
    }

    /**
     * 订单业务错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(OrderException.class)
    public Result orderException(OrderException e) {
        log.error("订单异常:orderNo:{},ex:{};", e.getOrderNo(), e.getMessage());
        log.error("订单异常", e);
        return Result.error().data("errcode", e.getCode()).msg(e.getMessage());
    }

    /**
     * 用户认证异常
     * @param e
     * @return
     */
    @ExceptionHandler(UserAuthException.class)
    public Result handleUserAuthException(UserAuthException e) {
        log.error("用户认证异常", e);
        return Result.error().msg(e.getMessage()).data("errcode", e.getCode());
    }

    /**
     * 业务异常的父类
     * @param e
     * @return
     */
    @ExceptionHandler(BizException.class)
    public Result BizException(BizException e) {
        log.error("业务异常", e);
        return Result.error().msg(e.getMessage()).data("errcode", e.getCode());
    }

    /**
     * 上传文件过大异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MultipartException.class)
    public Result handleException(MultipartException e) {
        log.error("文件上传错误", e);
        return Result.error().msg("上传文件不能超过5M");
    }

    /**
     * 生成小程序码错误
     * @param e
     * @return
     */
    @ExceptionHandler(WxErrorException.class)
    public Result wxErrorException(WxErrorException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("生成小程序码错误");
    }

    /**
     * 数据异常
     * @param e
     * @return
     */
    @ExceptionHandler(PayException.class)
    public Result handlePayException (PayException e) {
        log.error("支付异常", e);
        return Result.dataError().data("errcode", e.getCode()).msg(e.getMessage());
    }

    /**
     * 数据异常
     * @param e
     * @return
     */
    @ExceptionHandler(DataException.class)
    public Result handleSystemException(DataException e) {
        log.error("数据异常", e);
        return Result.dataError().data("errcode", e.getCode()).msg(e.getMessage());
    }

    /**
     * 统一异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("服务器错误!");
    }

}
