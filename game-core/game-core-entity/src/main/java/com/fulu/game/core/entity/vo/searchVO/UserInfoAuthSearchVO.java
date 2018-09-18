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
