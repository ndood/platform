package com.fulu.game.thirdparty.fenqile.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SignUtil {

    public enum SignType {
        MD5
    }

    public static String createSign(Map<String, Object> params, String signKey) {
        return createSign(params, "MD5", signKey);
    }


    public static String createSign(Map<String, Object> params, String signType, String signKey) {
        SortedMap<String, Object> sortedMap = new TreeMap<>(params);
        StringBuilder toSign = new StringBuilder();
        for (String key : sortedMap.keySet()) {
            String value = params.get(key).toString();
            toSign.append(key).append("=").append(value).append("&");
        }
        toSign.append("partner_key=").append(signKey);
        System.out.println("toSign:"+toSign);
        try {
            if (SignType.MD5.name().equalsIgnoreCase(signType)) {
                return DigestUtils.md5Hex(toSign.toString());
            } else {
                return DigestUtils.md5Hex(toSign.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



}
