package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.UserInfoAuthFile;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 信息认证表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:13
 */
@Data
public class UserInfoAuthVO extends UserInfoAuth {

    private String nickname;

    private Integer gender;

    private Integer age;

    private Integer userInfoAuth;

    private String userInfoAuthStr;

    //写真文件
    private List<UserInfoAuthFile> portraitList;
    //声音文件
    private List<UserInfoAuthFile> voiceList;

    //标签组
    private List<TagVO> groupTags;

    private String remark;

    //总服务金额
    private BigDecimal moneySum;
    //总单数
    private Integer orderCount;



    public String getUserInfoAuthStr() {
        return UserInfoAuthStatusEnum.getMsgByType(getUserInfoAuth());
    }

    public void setUserInfoAuthStr(String userInfoAuthStr) {
        this.userInfoAuthStr = userInfoAuthStr;
    }

}
