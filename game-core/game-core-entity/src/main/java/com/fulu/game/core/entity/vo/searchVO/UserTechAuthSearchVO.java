package com.fulu.game.core.entity.vo.searchVO;

import lombok.Data;

@Data
public class UserTechAuthSearchVO {

    private String nickname;
    private String mobile;
    private String startTime;
    private String endTime;
    private Integer categoryId;
    private Integer status;
    private String orderBy;
    private Integer userId;
    private Integer techLevelId;
    private Integer isActivate;
}
