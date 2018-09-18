package com.fulu.game.core.service.impl.payment;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 余额支付
 *
 * @author Gong ZeChun
 * @date 2018/9/13 18:25
 */
@Service
@Slf4j
public class BalancePayment {

    @Autowired
    UserService userService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;





    public boolean balancePayVirtualMoney(Integer userId, BigDecimal actualMoney, String orderNo) {
       return balancePayByUser(userId,actualMoney,orderNo,MoneyOperateTypeEnum.WITHDRAW_VIRTUAL_MONEY);
    }

    public boolean balancePayOrder(Integer userId, BigDecimal actualMoney, String orderNo) {
       return balancePayByUser(userId,actualMoney,orderNo,MoneyOperateTypeEnum.WITHDRAW_VIRTUAL_MONEY);
    }


    private boolean balancePayByUser(Integer userId, BigDecimal actualMoney, String orderNo,MoneyOperateTypeEnum moneyOperateTypeEnum){
        User user = userService.findById(userId);
        if (user == null) {
            log.info("当前用户id：{}查询数据库不存在", userId);
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        BigDecimal chargeBalance = user.getChargeBalance() == null ? BigDecimal.ZERO : user.getChargeBalance();
        BigDecimal balance = user.getBalance();
        BigDecimal totalBalance = chargeBalance.add(balance);

        if (totalBalance.compareTo(actualMoney) < 0) {
            log.error("用户userId：{}的账户余额不够充钻石，总余额：{}，应付金额：{}", userId, totalBalance, actualMoney);
            throw new PayException(PayException.ExceptionCode.BALANCE_NOT_ENOUGH_EXCEPTION);
        }

        //优先用不可提现余额来支付钻石
        if (chargeBalance.compareTo(actualMoney) >= 0) {
            user.setChargeBalance(chargeBalance.subtract(actualMoney));
        } else {
            user.setChargeBalance(BigDecimal.ZERO);
            user.setBalance(balance.subtract(actualMoney.subtract(chargeBalance)));
        }

        user.setUpdateTime(new Date());
        int result = userService.update(user);
        if(result<1){
            return false;
        }
        //记录零钱流水
        MoneyDetails mDetails = new MoneyDetails();
        mDetails.setOperatorId(user.getId());
        mDetails.setTargetId(user.getId());
        mDetails.setMoney(actualMoney.negate());
        mDetails.setAction(moneyOperateTypeEnum.getType());
        mDetails.setSum(user.getBalance().add(chargeBalance));
        mDetails.setRemark(moneyOperateTypeEnum.getMsg() + "订单号：" + orderNo);
        mDetails.setCreateTime(DateUtil.date());
        moneyDetailsService.drawSave(mDetails);
        return true;
    }




}
