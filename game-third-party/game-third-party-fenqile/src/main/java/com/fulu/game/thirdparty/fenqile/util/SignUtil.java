package com.fulu.game.thirdparty.fenqile.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SignUtil {

    public enum SignType{
        MD5
    }






    public static String createSign(Map<String, Object> params, String signType, String signKey){
        SortedMap<String, Object> sortedMap = new TreeMap<>(params);
        StringBuilder toSign = new StringBuilder();
        for (String key : sortedMap.keySet()) {
            String value = params.get(key).toString();
            toSign.append(key).append("=").append(value).append("&");
        }
        toSign.append("partner_key=").append(signKey);
        if (SignType.MD5.name().equalsIgnoreCase(signType)) {
            return DigestUtils.md5Hex(toSign.toString()).toUpperCase();
        } else {
            return DigestUtils.md5Hex(toSign.toString()).toUpperCase();
        }
    }
}
