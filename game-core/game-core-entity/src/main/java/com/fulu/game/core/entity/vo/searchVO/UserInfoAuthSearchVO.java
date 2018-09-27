package com.fulu.game.core.entity.vo.searchVO;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class UserInfoAuthSearchVO {

    private Integer id;
    private String nickname;
    private Integer userId;
    private String mobile;
    private String startTime;
    private String endTime;
    private Integer userInfoAuth;
    private Integer gender;
    private Integer startAge;
    private Integer endAge;
    private String orderBy;
    private Boolean userAutoSetting;
    /**
     * //1:普通用户,2:陪玩师
     */
    private Integer type;
    //自动问好
    private String autoSayHello;
    //是否开启代聊  0关闭  1开启
    private Boolean openSubstituteIm;

    //注册来源名
    private String sourceName;

    /**
     * 外链id
     */
    private Integer sourceId;
    private Integer isPlatformShow;


    public String getOrderBy() {
        if (orderBy != null) {
            return StrUtil.toUnderlineCase(orderBy);
        }
        return orderBy;
    }

    private String imId;
    private String imPsw;
    private Integer imSubstituteId;
}
