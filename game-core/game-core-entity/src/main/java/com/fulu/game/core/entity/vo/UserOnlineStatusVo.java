package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Product;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;


/**
 * 用户在线状态
 *
 * @author jaycee.Deng
 */
@Data
public class UserOnlineStatusVo {

    private Integer userId;
    
    private Boolean isOnLine;

}
