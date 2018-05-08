package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.CashProcessStatusEnum;
import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.enums.exception.CashExceptionEnums;
import com.fulu.game.common.enums.exception.ParamsExceptionEnums;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.CashDrawsDao;
import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.service.CashDrawsService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("/cashDrawsService")
public class CashDrawsServiceImpl extends AbsCommonService<CashDraws,Integer> implements CashDrawsService {

    @Autowired
	private CashDrawsDao cashDrawsDao;
    @Autowired
    private UserService userService;
    @Autowired
    private MoneyDetailsService mdService;

    @Override
    public ICommonDao<CashDraws, Integer> getDao() {
        return cashDrawsDao;
    }

    /**
     * 1 判断提现金额参数是否合法
     * 2 是否超过账户余额
     * 3 生成t_cash_draws记录
     * 4 修改提现后余额，生成t_money_details记录
     * 5 修改t_user记录
     * @param cashDrawsVO
     * @return
     */
    @Override
    public CashDraws save(CashDrawsVO cashDrawsVO){
        BigDecimal money = cashDrawsVO.getMoney();
        if(money.compareTo(BigDecimal.ZERO)==-1){
            throw new CashException(CashExceptionEnums.CASH_NEGATIVE_EXCEPTION);
        }
        User user = userService.getCurrentUser();
        BigDecimal balance = user.getBalance();
        if (money.compareTo(balance)==1){
            throw new CashException(CashExceptionEnums.CASH_EXCEED_EXCEPTION);
        }
        //提现后的余额
        BigDecimal newBalance = balance.subtract(money);
        CashDraws cashDraws = new CashDraws();
        BeanUtil.copyProperties(cashDrawsVO,cashDraws);
        cashDraws.setUserId(user.getId());
        cashDraws.setNickname(user.getNickname());
        cashDraws.setMobile(user.getMobile());
        cashDraws.setCashStatus(CashProcessStatusEnum.WAITING.getType());
        cashDraws.setCreateTime(new Date());
        cashDrawsDao.create(cashDraws);

        MoneyDetails moneyDetails = new MoneyDetails();
        moneyDetails.setOperatorId(user.getId());
        moneyDetails.setTargetId(user.getId());
        moneyDetails.setAction(MoneyOperateTypeEnum.USER_DRAW_CASH.getType());
        moneyDetails.setMoney(money);
        moneyDetails.setSum(newBalance);
        moneyDetails.setCashId(cashDraws.getCashId());
        moneyDetails.setCreateTime(new Date());
        mdService.drawSave(moneyDetails);

        user.setBalance(newBalance);
        userService.update(user);
        userService.updateRedisUser(user);
        return cashDraws;
    }

    @Override
    public PageInfo<CashDraws> list(CashDrawsVO cashDrawsVO,Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize,cashDrawsVO.getOrderBy());
        List<CashDraws> list = cashDrawsDao.findByParameter(cashDrawsVO);
        return new PageInfo(list);
    }

    /**
     * 管理员-打款
     * @param cashId
     * @param comment
     * @return
     */
    @Override
    public CashDraws draw(Integer cashId, String comment){
        CashDraws cashDraws = findById(cashId);
        if (null == cashDraws){
            return null;
        }
        cashDraws.setOperator("admin");
        cashDraws.setComment(comment);
        cashDraws.setCashStatus(CashProcessStatusEnum.DONE.getType());//修改为已处理状态
        cashDraws.setCashNo("");//订单处理号暂做保留
        cashDraws.setProcessTime(new Date());
        cashDrawsDao.update(cashDraws);
        return cashDraws;
    }
	
}
