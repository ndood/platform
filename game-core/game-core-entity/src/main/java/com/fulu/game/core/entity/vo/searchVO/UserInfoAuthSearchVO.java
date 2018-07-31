package com.fulu.game.core.entity.vo.searchVO;

import com.xiaoleilu.hutool.util.StrUtil;
import lombok.Data;

@Data
public class UserInfoAuthSearchVO {

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


    public String getOrderBy() {
        if (orderBy != null) {
            return StrUtil.toUnderlineCase(orderBy);
        }
        return orderBy;
    }
}
