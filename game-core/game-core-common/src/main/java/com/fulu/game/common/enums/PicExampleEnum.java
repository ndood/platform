package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PicExampleEnum {

    CATEGORY_EXAMPLE_DOTA2(1,"41","https://game-play.oss-cn-hangzhou.aliyuncs.com/example/dota2.jpg"),
    CATEGORY_EXAMPLE_DOTA2_TEST(1,"45",CATEGORY_EXAMPLE_DOTA2.getPicUrl()),
    CATEGORY_EXAMPLE_CJZC(1,"31","https://game-play.oss-cn-hangzhou.aliyuncs.com/example/cjzc.jpg"),
    CATEGORY_EXAMPLE_LOL(1,"33","https://game-play.oss-cn-hangzhou.aliyuncs.com/example/lol.jpg"),
    CATEGORY_EXAMPLE_PUBG(1,"32","https://game-play.oss-cn-hangzhou.aliyuncs.com/example/pubg.jpg"),
    CATEGORY_EXAMPLE_WZRY(1,"30","https://game-play.oss-cn-hangzhou.aliyuncs.com/example/wzry.jpg"),
    CATEGORY_EXAMPLE_DEFAULT(1,"default","https://game-play.oss-cn-hangzhou.aliyuncs.com/example/wzry.jpg"),
    CATEGORY_EXAMPLE_CJZC_TEST(1,"31",CATEGORY_EXAMPLE_CJZC.getPicUrl());

    private Integer type;
    private String key;
    private String picUrl;


    public static String getPicUrlByTypeAndKey(Integer type, String key){
        for(PicExampleEnum picExample : PicExampleEnum.values()){
            if(picExample.type.equals(type)&&picExample.key.equals(key)){
                return picExample.getPicUrl();
            }
        }
        return getDefTypePicUrl(type);
    }


    public static String getDefTypePicUrl(Integer type){
        for(PicExampleEnum picExample : PicExampleEnum.values()){
            if(picExample.type.equals(type)&&picExample.key.equals("default")){
                return picExample.getPicUrl();
            }
        }
        return null;
    }
}
