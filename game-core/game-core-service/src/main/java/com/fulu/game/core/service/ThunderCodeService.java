package com.fulu.game.core.service;

import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.ThunderCode;
import com.fulu.game.core.entity.vo.ThunderCodeVO;


/**
 * 迅雷活动兑换码
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-10-12 09:50:54
 */
public interface ThunderCodeService extends ICommonService<ThunderCode, Integer> {

    ThunderCode findByCode(String code, Integer type);

    ThunderCodeVO getTwoHoursWelfare();

    ThunderCodeVO getOneOrderWelfare();

    ThunderCodeVO getThreeOrderWelfare();

    Coupon newUserCoupon(String ipStr);
}
