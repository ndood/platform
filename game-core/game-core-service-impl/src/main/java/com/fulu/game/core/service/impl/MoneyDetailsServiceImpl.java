package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.exception.CashExceptionEnums;
import com.fulu.game.common.enums.exception.UserExceptionEnums;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.MoneyDetailsDao;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.service.MoneyDetailsService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("moneyDetailsService")
public class MoneyDetailsServiceImpl extends AbsCommonService<MoneyDetails,Integer> implements MoneyDetailsService {

    @Autowired
	private MoneyDetailsDao moneyDetailsDao;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<MoneyDetails, Integer> getDao() {
        return moneyDetailsDao;
    }

    @Override
    public PageInfo<MoneyDetailsVO> listByAdmin(MoneyDetailsVO moneyDetailsVO, Integer pageSize, Integer pageNum){
        PageHelper.startPage(pageNum, pageSize,moneyDetailsVO.getOrderBy());
        List<MoneyDetailsVO> list = moneyDetailsDao.findByAdmin(moneyDetailsVO);
        return new PageInfo(list);
    }

    @Override
    public PageInfo<MoneyDetailsVO> listByUser(MoneyDetailsVO moneyDetailsVO, Integer pageSize, Integer pageNum){
        PageHelper.startPage(pageNum, pageSize,moneyDetailsVO.getOrderBy());
        List<MoneyDetailsVO> list = moneyDetailsDao.findByUser(moneyDetailsVO);
        return new PageInfo(list);
    }

    /**
     * 管理员加零钱
     * @param moneyDetailsVO
     * @return
     */
    @Override
    public MoneyDetails save(MoneyDetailsVO moneyDetailsVO) {
        if (moneyDetailsVO.getMoney().compareTo(BigDecimal.ZERO)==-1){
            throw new CashException(CashExceptionEnums.CASH_NEGATIVE_EXCEPTION);
        }
        User user = userService.findByMobile(moneyDetailsVO.getMobile());
        if (null == user){
            throw new UserException(UserExceptionEnums.USER_NOT_EXIST_EXCEPTION);
        }
        //加钱之前该用户的零钱
        BigDecimal balance = user.getBalance();
        BigDecimal newBalance = balance.add(moneyDetailsVO.getMoney());

        MoneyDetails moneyDetails = new MoneyDetails();
        BeanUtil.copyProperties(moneyDetailsVO,moneyDetails);
        moneyDetails.setSum(newBalance);
        moneyDetails.setOperatorId(1);//查询当前管理员对象后修改掉
        moneyDetails.setTargetId(user.getId());
        moneyDetails.setAction(1);
        moneyDetails.setCreateTime(new Date());
        moneyDetailsDao.create(moneyDetails);
        user.setBalance(newBalance);
        userService.update(user);
        return moneyDetails;
    }

    /**
     * 陪玩订单完成生成零钱记录
     * @param moneyDetailsVO
     * @return
     */
    @Override
    public MoneyDetails orderSave(MoneyDetailsVO moneyDetailsVO) {
        if (moneyDetailsVO.getMoney().compareTo(BigDecimal.ZERO)==-1){
            throw new CashException(CashExceptionEnums.CASH_NEGATIVE_EXCEPTION);
        }
        User user = userService.findById(moneyDetailsVO.getTargetId());
        if (null == user){
            throw new UserException(UserExceptionEnums.USER_NOT_EXIST_EXCEPTION);
        }
        //加钱之前该用户的零钱
        BigDecimal balance = user.getBalance();
        BigDecimal newBalance = balance.add(moneyDetailsVO.getMoney());

        MoneyDetails moneyDetails = new MoneyDetails();
        BeanUtil.copyProperties(moneyDetailsVO,moneyDetails);
        moneyDetails.setSum(newBalance);
        moneyDetails.setOperatorId(moneyDetails.getTargetId());
        moneyDetails.setAction(2);//2表示陪玩订单
        moneyDetails.setCreateTime(new Date());
        moneyDetailsDao.create(moneyDetails);
        user.setBalance(newBalance);
        userService.update(user);
        return moneyDetails;
    }

    @Override
    public MoneyDetails drawSave(MoneyDetails moneyDetails) {
        moneyDetailsDao.create(moneyDetails);
        return moneyDetails;
    }


}
