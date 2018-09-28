package com.fulu.game.core.entity.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fulu.game.core.entity.ActivityCoupon;
import com.fulu.game.core.entity.OfficialActivity;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * 官方公告
 *
 * @author wangbin
 * @date 2018-09-28 14:32:06
 */
@Data
public class OfficialActivityVO  extends OfficialActivity {


    List<ActivityCoupon> couponList;

    @JsonIgnore
    List<String> redeemCodes;

    @JsonIgnore
    private Date currentTime;

}
