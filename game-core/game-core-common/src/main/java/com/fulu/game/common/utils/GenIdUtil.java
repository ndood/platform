package com.fulu.game.common.utils;

import java.util.UUID;

/**
 * 生成唯一性id的工具类
 */
public class GenIdUtil {

    /**
     * 生成token
     *
     * @return
     */
    public static String GetGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
