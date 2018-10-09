package com.fulu.game.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.CashProcessStatusEnum;
import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.MoneyDetailsDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("moneyDetailsService")
@Slf4j
public class MoneyDetailsServiceImpl extends AbsCommonService<MoneyDetails, Integer> implements MoneyDetailsService {

    @Autowired
    private MoneyDetailsDao moneyDetailsDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    @Override
    public ICommonDao<MoneyDetails, Integer> getDao() {
        return moneyDetailsDao;
    }

    @Override
    public PageInfo<MoneyDetailsVO> listByAdmin(MoneyDetailsVO moneyDetailsVO, Integer pageSize, Integer pageNum) {
        if(moneyDetailsVO.getAction() == null){
            List<Integer> actions = new ArrayList<>();
            actions.add(MoneyOperateTypeEnum.ADMIN_ADD_CHANGE.getType());
            actions.add(MoneyOperateTypeEnum.ADMIN_SUBTRACT_CHANGE.getType());
            moneyDetailsVO.setActions(actions);
        }
//        moneyDetailsVO.setAction(MoneyOperateTypeEnum.ADMIN_ADD_CHANGE.getType());
        String orderBy = "tmd.create_time desc";
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize, orderBy);
        } else {
            PageHelper.orderBy(orderBy);
        }
        List<MoneyDetailsVO> list = moneyDetailsDao.findByAdmin(moneyDetailsVO);
        return new PageInfo(list);
    }

    @Override
    public PageInfo<MoneyDetailsVO> listByUser(MoneyDetailsVO moneyDetailsVO, Integer pageSize, Integer pageNum) {
        String orderBy = "tmd.create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<MoneyDetailsVO> list = moneyDetailsDao.findByUser(moneyDetailsVO);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        for (MoneyDetailsVO vo : list) {
            if (vo.getAction().equals(MoneyOperateTypeEnum.USER_DRAW_CASH.getType())) {
                if (vo.getCashStatus() == null) {
                    vo.setCashStatusMsg(CashProcessStatusEnum.WAITING.getMsg());
                } else {
                    if (vo.getCashStatus().equals(CashProcessStatusEnum.DONE.getType())) {
                        vo.setCashStatusMsg(CashProcessStatusEnum.DONE.getMsg());
                    } else if (vo.getCashStatus().equals(CashProcessStatusEnum.REFUSE.getType())) {
                        vo.setCashStatusMsg(CashProcessStatusEnum.REFUSE.getMsg());
                    } else {
                        vo.setCashStatusMsg(CashProcessStatusEnum.WAITING.getMsg());
                    }
                }
                //todo gzc 判断逻辑很繁琐、后续优化
            } else if (vo.getAction().equals(MoneyOperateTypeEnum.ADMIN_REFUSE_REMIT.getType())) {
                vo.setCashStatusMsg(CashProcessStatusEnum.REFUND.getMsg());
            }else{
                vo.setCashStatusMsg(MoneyOperateTypeEnum.getMsgByType(vo.getAction()));
            }
        }
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
        Admin admin = adminService.getCurrentUser();
        log.info("调用管理员加零钱接口，管理员id:{}", admin.getId());
        User user = userService.findByMobile(moneyDetailsVO.getMobile());
        if (null == user) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(user.getId());
        //加钱之前该用户的零钱
        BigDecimal balance = user.getBalance();
        BigDecimal newBalance = balance.add(moneyDetailsVO.getMoney());
        MoneyDetails moneyDetails = new MoneyDetails();
        BeanUtil.copyProperties(moneyDetailsVO, moneyDetails);
        BigDecimal chargeBalance = user.getChargeBalance() == null ? BigDecimal.ZERO : user.getChargeBalance();
        moneyDetails.setSum(newBalance.add(chargeBalance));
        moneyDetails.setOperatorId(admin.getId());
        moneyDetails.setTargetId(user.getId());
        if(userInfoAuth != null && userInfoAuth.getVestFlag()){
            moneyDetails.setUserType(UserTypeEnum.VEST_USER.getType());
        } else {
            moneyDetails.setUserType(UserTypeEnum.GENERAL_USER.getType());
        }
        // 当action为null时，设置为用户新增零钱
        BigDecimal money = null;
        if(moneyDetails.getAction() == null){
            moneyDetails.setAction(MoneyOperateTypeEnum.ADMIN_ADD_CHANGE.getType());
            log.info("当前余额:{},加零钱金额:{}", balance, moneyDetailsVO.getMoney());
            // 平台扣钱
            money = moneyDetails.getMoney().negate();
        } else {
            log.info("当前余额:{},扣除零钱金额:{}", balance, moneyDetailsVO.getMoney());
            newBalance = balance.subtract(moneyDetailsVO.getMoney());
            // 平台加钱
            money = moneyDetails.getMoney();
        }
        moneyDetails.setCreateTime(new Date());
        moneyDetailsDao.create(moneyDetails);
        user.setBalance(newBalance);
        userService.update(user);
        log.info("更新用户余额完成，加零钱后余额:{}", user.getBalance());
        //计入平台支出流水
        platformMoneyDetailsService.createSmallChangeDetails(moneyDetails.getRemark(), user.getId(), money);
        log.info("调用平台支出流水接口执行结束");
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
        log.info("调用陪玩订单完成生成提现记录接口，入参金额money:{},入账人id:{},订单号:{}", money, targetId, orderNo);
        if (money.compareTo(BigDecimal.ZERO) == -1) {
            throw new CashException(CashException.ExceptionCode.CASH_NEGATIVE_EXCEPTION);
        }
        User user = userService.findById(targetId);
        if (null == user) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        //加钱之前该用户的零钱
        BigDecimal balance = user.getBalance();
        BigDecimal newBalance = balance.add(money);
        MoneyDetails moneyDetails = new MoneyDetails();
        moneyDetails.setMoney(money);
        moneyDetails.setOperatorId(targetId);
        moneyDetails.setTargetId(targetId);
        moneyDetails.setAction(MoneyOperateTypeEnum.ORDER_COMPLETE.getType());
        BigDecimal chargeBalance = user.getChargeBalance() == null ? BigDecimal.ZERO : user.getChargeBalance();
        moneyDetails.setSum(newBalance.add(chargeBalance));
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
    public List<MoneyDetails> findUserMoneyByAction(Integer targetId, Date startTime, Date endTime) {
        List<Integer> actionList = Lists.newArrayList(MoneyOperateTypeEnum.ORDER_COMPLETE.getType(), MoneyOperateTypeEnum.ADMIN_ADD_CHANGE.getType());
        return moneyDetailsDao.findUserMoneyByAction(targetId, actionList, startTime, endTime);
    }

    @Override
    public BigDecimal weekIncome(Integer targetId) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        List<MoneyDetails> moneyDetailsList = findUserMoneyByAction(targetId, startTime, endTime);
        BigDecimal sum = new BigDecimal(0);
        for (MoneyDetails moneyDetails : moneyDetailsList) {
            sum = sum.add(moneyDetails.getMoney());
        }
        return sum;
    }

    @Override
    public PageInfo<MoneyDetails> listUserDetails(MoneyDetailsVO moneyDetailsVO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<MoneyDetails> list = moneyDetailsDao.findByParameter(moneyDetailsVO);
        return new PageInfo<>(list);
    }

}
