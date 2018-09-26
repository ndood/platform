package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.UserInfoAuthFile;
import com.fulu.game.core.entity.UserTechAuth;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    private String birth;

    private String mobile;

    private String constellation;

    private Integer age;

    private String headUrl;

    private Integer userInfoAuth;

    //总服务金额
    private BigDecimal moneySum;
    //总单数
    private Integer orderCount;
    //技能认证数
    private Integer techAuthCount;
    //审核状态
    private String userInfoAuthStr;

    //写真文件
    private List<UserInfoAuthFile> portraitList;
    //声音文件
    private List<UserInfoAuthFile> voiceList;
    //视频文件
    private List<UserInfoAuthFile> videoList;

    //标签组
    private List<TagVO> groupTags;

    private String remark;

    private List<UserTechAuth> userTechAuthList;


    public String getUserInfoAuthStr() {
        return UserInfoAuthStatusEnum.getMsgByType(getUserInfoAuth());
    }

    public void setUserInfoAuthStr(String userInfoAuthStr) {
        this.userInfoAuthStr = userInfoAuthStr;
    }
    
    private String imId;

    private String imPsw;
    
    private Long unreadCount;
    
    private Integer groupPicCount;
    
    private Integer recommendProductId;

    //综合得星评分数
    private BigDecimal scoreAvg;
    // 注册来源类型
    private Integer registerType;
    //关注用户数
    private Integer attentions;
    //粉丝数
    private Integer fansCount;
    //虚拟粉丝数
    private Integer virtualFansCount;
    //历史浏览次数
    private Integer historyBrowseCount;
    //历史被访问次数
    private Integer historyAccessedCount;
    //发布动态数
    private Integer dynamicCount;
    //总消费金额
    private BigDecimal expendMoneySum;
    //总消费订单数
    private Integer expendOrderCount;
    //可提现余额
    private BigDecimal balance;
    //充值零钱（不可提现）
    private BigDecimal chargeBalance;

    private List<PicGroupVO> groupList = new ArrayList<>();
}
