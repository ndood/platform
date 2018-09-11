package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualPayOrderDao;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.service.VirtualPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class VirtualPayOrderServiceImpl extends AbsCommonService<VirtualPayOrder, Integer> implements VirtualPayOrderService {

    @Autowired
    private VirtualPayOrderDao virtualPayOrderDao;

    @Override
    public ICommonDao<VirtualPayOrder, Integer> getDao() {
        return virtualPayOrderDao;
    }

    public VirtualPayOrder charge(String code, BigDecimal actualMoney, Integer virtualMoney, String mobile) {
//        if (StringUtils.isBlank(code)) {
//            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
//        }
//
//        WxMpService wxMpService = wxMpServiceSupply.getWxMpService();
//        WxMpOAuth2AccessToken token;
//        String openId = null;
//        String unionId = null;
//        try {
//            token = wxMpService.oauth2getAccessToken(code);
//            openId = token.getOpenId();
//            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(token, null);
//            unionId = wxMpUser.getUnionId();
//        } catch (WxErrorException e) {
//            e.printStackTrace();
//            log.error("通过公众号获取用户信息出错", e);
//        }

        //fixme 先判断mobile 然后openId 然后unionId（非空判断）

        return null;
    }

    @Override
    public VirtualPayOrder findByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }

        return virtualPayOrderDao.findByOrderNo(orderNo);
    }
}
