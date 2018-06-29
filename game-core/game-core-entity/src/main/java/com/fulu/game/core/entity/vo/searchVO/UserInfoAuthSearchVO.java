package com.fulu.game.core.entity.vo.searchVO;

import lombok.Data;

@Data
public class UserInfoAuthSearchVO {

    private String nickname;
    private Integer userId;
    private String mobile;
    private String startTime;
    private String endTime;
    private String orderBy = "update_time desc";
}
