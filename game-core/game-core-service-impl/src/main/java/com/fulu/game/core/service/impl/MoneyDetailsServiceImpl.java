package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.enums.exception.CashExceptionEnums;
import com.fulu.game.common.enums.exception.UserExceptionEnums;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.MoneyDetailsDao;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.PlatformMoneyDetailsService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("moneyDetailsService")
public class MoneyDetailsServiceImpl extends AbsCommonService<MoneyDetails, Integer> implements MoneyDetailsService {

    @Autowired
    private MoneyDetailsDao moneyDetailsDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;

    @Override
    public ICommonDao<MoneyDetails, Integer> getDao() {
        return moneyDetailsDao;
    }

    @Override
    public PageInfo<MoneyDetailsVO> listByAdmin(MoneyDetailsVO moneyDetailsVO, Integer pageSize, Integer pageNum) {
        moneyDetailsVO.setAction(MoneyOperateTypeEnum.ADMIN_ADD_CHANGE.getType());
        String orderBy = "tmd.create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<MoneyDetailsVO> list = moneyDetailsDao.findByAdmin(moneyDetailsVO);
        return new PageInfo(list);
    }

    @Override
    public PageInfo<MoneyDetailsVO> listByUser(MoneyDetailsVO moneyDetailsVO, Integer pageSize, Integer pageNum) {
        String orderBy = "tmd.create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<MoneyDetailsVO> list = moneyDetailsDao.findByUser(moneyDetailsVO);
        return new PageInfo(list);
    }

    /**
     * 管理员加零钱
     *
     * @param moneyDetailsVO
     * @return
     */
    @Override
    public MoneyDetails save(MoneyDetailsVO moneyDetailsVO) {
        User user = userService.findByMobile(moneyDetailsVO.getMobile());
        if (null == user) {
            throw new UserException(UserExceptionEnums.USER_NOT_EXIST_EXCEPTION);
        }
        //加钱之前该用户的零钱
        BigDecimal balance = user.getBalance();
        BigDecimal newBalance = balance.add(moneyDetailsVO.getMoney());

        MoneyDetails moneyDetails = new MoneyDetails();
        BeanUtil.copyProperties(moneyDetailsVO, moneyDetails);
        moneyDetails.setSum(newBalance);
        moneyDetails.setOperatorId(adminService.getCurrentUser().getId());//查询当前管理员对象后修改掉
        moneyDetails.setTargetId(user.getId());
        moneyDetails.setAction(MoneyOperateTypeEnum.ADMIN_ADD_CHANGE.getType());
        moneyDetails.setCreateTime(new Date());
        moneyDetailsDao.create(moneyDetails);
        user.setBalance(newBalance);
        userService.update(user);

        //计入平台支出流水
        platformMoneyDetailsService.createSmallChangeDetails(moneyDetails.getRemark(),user.getId(),moneyDetails.getMoney().negate());
        return moneyDetails;
    }

    /**
     * 陪玩订单完成生成零钱记录
     *
     * @param money
     * @param targetId
     * @param orderNo
     * @return
     */
    @Override
    public MoneyDetails orderSave(BigDecimal money, Integer targetId, String orderNo) {
        if (money.compareTo(BigDecimal.ZERO) == -1) {
            throw new CashException(CashExceptionEnums.CASH_NEGATIVE_EXCEPTION);
        }
        User user = userService.findById(targetId);
        if (null == user) {
            throw new UserException(UserExceptionEnums.USER_NOT_EXIST_EXCEPTION);
        }
        //加钱之前该用户的零钱
        BigDecimal balance = user.getBalance();
        BigDecimal newBalance = balance.add(money);

        MoneyDetails moneyDetails = new MoneyDetails();
        moneyDetails.setMoney(money);
        moneyDetails.setOperatorId(targetId);//默认是系统加款
        moneyDetails.setTargetId(targetId);
        moneyDetails.setAction(MoneyOperateTypeEnum.ORDER_COMPLETE.getType());//2表示陪玩订单
        moneyDetails.setSum(newBalance);
        moneyDetails.setRemark("陪玩订单完成，系统打款,订单号_" + orderNo);
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

    @Override
    public List<MoneyDetails> list(Integer targetId, Date startTime, Date endTime) {
        MoneyDetailsVO moneyDetailsVO = new MoneyDetailsVO();
        moneyDetailsVO.setTargetId(targetId);
        moneyDetailsVO.setStartTime(startTime);
        moneyDetailsVO.setEndTime(endTime);
        return moneyDetailsDao.findByParameter(moneyDetailsVO);
    }

    @Override
    public BigDecimal weekIncome(Integer targetId) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        List<MoneyDetails> moneyDetailsList = list(targetId, startTime, endTime);
        BigDecimal sum = new BigDecimal(0);
        for (MoneyDetails moneyDetails : moneyDetailsList) {
            sum = sum.add(moneyDetails.getMoney());
        }
        return sum;
    }


}
