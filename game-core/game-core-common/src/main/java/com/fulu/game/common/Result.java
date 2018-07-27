package com.fulu.game.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果封装
 */
public class Result implements Serializable {

    private int status;

    private String msg ="";

    private Object data = null;

    public Result(int status) {
        this.status = status;
    }

    public Result() {
    }

    public Result msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result data(Object data) {
        this.data = data;
        return this;
    }

    public Result data(String key,Object data) {
        Map<String,Object> map = new HashMap<>();
        map.put(key, data);
        this.data = map;
        return this;
    }

    public static Result success() {
        Result result = new Result();
        result.setStatus(ResultStatus.SUCCESS);
        return result;
    }

    public static Result error() {
        Result result = new Result();
        result.setStatus(ResultStatus.ERROR);
        return result;
    }

    public static Result error(int code) {
        Result result = new Result();
        result.setStatus(code);
        return result;
    }

    public static Result noLogin() {
        Result result = new Result();
        result.setStatus(ResultStatus.NOLOGIN);
        result.setMsg("登录状态已失效,请重新登录!");
        return result;
    }

    public static Result noUnionId() {
        Result result = new Result();
        result.setStatus(ResultStatus.NOUNIONID);
        result.setMsg("信息不完善，请完善信息!");
        return result;
    }

    public static Result accessDeny() {
        Result result = new Result();
        result.setStatus(ResultStatus.ACCESS_DENY);
        return result;
    }

    public static Result newUser() {
        Result result = new Result();
        result.setStatus(ResultStatus.NEWUSER);
        return result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
