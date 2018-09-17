package com.fulu.game.core.service.impl.payment;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.BalancePayService;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 余额支付
 *
 * @author Gong ZeChun
 * @date 2018/9/13 18:25
 */
@Service
@Slf4j
public class BalancePayServiceImpl implements BalancePayService {

    @Autowired
    UserService userService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;



    @Override
    public boolean balancePay(Integer userId, BigDecimal actualMoney, String orderNo) {
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

        user.setUpdateTime(DateUtil.date());
        userService.update(user);

        //记录零钱流水
        MoneyDetails mDetails = new MoneyDetails();
        mDetails.setOperatorId(userId);
        mDetails.setTargetId(userId);
        mDetails.setMoney(actualMoney.negate());
        mDetails.setAction(MoneyOperateTypeEnum.WITHDRAW_VIRTUAL_MONEY.getType());
        mDetails.setSum(user.getBalance().add(chargeBalance));
        mDetails.setRemark(MoneyOperateTypeEnum.WITHDRAW_VIRTUAL_MONEY.getMsg() + "订单号：" + orderNo);
        mDetails.setCreateTime(DateUtil.date());
        moneyDetailsService.drawSave(mDetails);

        return true;
    }
}
