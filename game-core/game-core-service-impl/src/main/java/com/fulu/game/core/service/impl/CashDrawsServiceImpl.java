package com.fulu.game.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.CashDrawsDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.entity.vo.UserBodyAuthVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Service("/cashDrawsService")
@Slf4j
public class CashDrawsServiceImpl extends AbsCommonService<CashDraws, Integer> implements CashDrawsService {

    @Autowired
    private CashDrawsDao cashDrawsDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private MoneyDetailsService mdService;
    @Autowired
    private UserBodyAuthService userBodyAuthService;

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
     *
     * @param cashDrawsVO
     * @return
     */
    @Override
    public CashDrawsVO save(CashDrawsVO cashDrawsVO) {
        log.info("====调用提现申请接口====");
        User user = userService.getCurrentUser();
        if (null == user) {
            log.error("提款申请异常，当前操作用户不存在");
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        BigDecimal money = cashDrawsVO.getMoney();
        if (money.compareTo(BigDecimal.ZERO) == -1) {
            log.error("提款申请异常，提款金额小于0，用户id:{}", user.getId());
            throw new CashException(CashException.ExceptionCode.CASH_NEGATIVE_EXCEPTION);
        }

        UserBodyAuthVO uba = new UserBodyAuthVO();
        uba.setUserId(user.getId());
        List<UserBodyAuth> list = userBodyAuthService.findByParameter(uba);

        int authStatus = UserBodyAuthStatusEnum.NO_AUTH.getType().intValue();

        if (CollectionUtils.isNotEmpty(list)) {
            UserBodyAuth authInfo = list.get(0);
            authStatus = authInfo.getAuthStatus().intValue();
        }

        if (authStatus != UserBodyAuthStatusEnum.AUTH_SUCCESS.getType().intValue()) {
            log.error("提款申请异常，当前操作用户未进行身份认证");
            throw new UserException(UserException.ExceptionCode.BODY_NO_AUTH);
        }


        user = userService.findById(user.getId());
        BigDecimal balance = user.getBalance();
        if (money.compareTo(balance) == 1) {
            log.error("提款申请异常，金额超出账户余额，用户id:{}", user.getId());
            throw new CashException(CashException.ExceptionCode.CASH_EXCEED_EXCEPTION);
        }
        log.info("====提现用户id:{},提现金额:{},账户余额:{}", user.getId(), money, balance);
        //提现后的余额
        BigDecimal newBalance = balance.subtract(money);
        CashDraws cashDraws = new CashDraws();
        BeanUtil.copyProperties(cashDrawsVO, cashDraws);
        cashDraws.setUserId(user.getId());
        cashDraws.setNickname(user.getNickname());
        cashDraws.setMobile(user.getMobile());
        cashDraws.setCashStatus(CashProcessStatusEnum.WAITING.getType());
        cashDraws.setCreateTime(new Date());
        cashDrawsDao.create(cashDraws);
        log.info("生成提款申请记录");
        MoneyDetails moneyDetails = new MoneyDetails();
        moneyDetails.setOperatorId(user.getId());
        moneyDetails.setTargetId(user.getId());
        moneyDetails.setAction(MoneyOperateTypeEnum.USER_DRAW_CASH.getType());
        moneyDetails.setMoney(money.negate());
        moneyDetails.setSum(newBalance);
        moneyDetails.setCashId(cashDraws.getCashId());
        moneyDetails.setCreateTime(new Date());
        mdService.drawSave(moneyDetails);
        log.info("记录账户提款流水至t_money_details");
        user.setBalance(newBalance);
        userService.update(user);
        log.info("更新用户余额");
        userService.updateRedisUser(user);
        log.info("更新redisUser");
        CashDrawsVO vo = new CashDrawsVO();
        BeanUtil.copyProperties(cashDraws, vo);
        vo.setTips(getCashDrawsTips());
        return vo;
    }

    /**
     * 根据提现时间获取提醒文案
     * 注：每周二、周五进行打款（周二办理周四-周日的提现申请，周五办理周一到周三的提现申请）
     *
     * @return 返回文案
     */
    private String getCashDrawsTips() {
        int weekInt = DateUtil.thisDayOfWeekEnum().getValue();
        boolean flag = weekInt >= Week.MONDAY.getValue() && weekInt <= Week.WEDNESDAY.getValue();
        if (flag) {
            return Constant.CASH_DRAWS_NEXT_FRIDAY_TIPS;
        }
        return Constant.CASH_DRAWS_NEXT_TUESDAY_TIPS;
    }

    @Override
    public PageInfo<CashDrawsVO> list(CashDrawsVO cashDrawsVO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "t1.create_time DESC");
        List<CashDrawsVO> list = cashDrawsDao.findDetailByParameter(cashDrawsVO);

        this.charmToMoney(list);

        return new PageInfo(list);
    }

    @Override
    public PageInfo<CashDrawsVO> list(CashDrawsVO cashDrawsVO) {
        PageHelper.orderBy("t1.create_time DESC");

        List<CashDrawsVO> list = cashDrawsDao.findDetailByParameter(cashDrawsVO);
        this.charmToMoney(list);

        return new PageInfo(list);
    }

    /**
     * 魅力值转换成金额
     *
     * @param list
     */
    private void charmToMoney(List<CashDrawsVO> list) {
        
        //转换魅力值的可提现金额   魅力值除以10*70%就是可提现金额
        for (int i = 0; i < list.size(); i++) {
            BigDecimal charm = list.get(i).getCharm();
            charm = charm.multiply(new BigDecimal("0.07"));
            list.get(i).setCharm(charm.setScale(2,BigDecimal.ROUND_DOWN));
        }
    }


    /**
     * 管理员-打款
     *
     * @param cashId
     * @param comment
     * @return
     */
    @Override
    public CashDraws draw(Integer cashId, String comment) {
        Admin admin = adminService.getCurrentUser();
        log.info("调用打款接口,入参cashId:{},操作人:{}", cashId, admin.getId());
        CashDraws cashDraws = findById(cashId);
        if (null == cashDraws) {
            return null;
        }
        cashDraws.setOperator(admin.getName());
        cashDraws.setComment(comment);
        cashDraws.setCashStatus(CashProcessStatusEnum.DONE.getType());//修改为已处理状态
        cashDraws.setCashNo(null);//订单处理号暂做保留
        cashDraws.setProcessTime(new Date());
        cashDrawsDao.update(cashDraws);
        return cashDraws;
    }

    @Override
    public boolean refuse(Integer cashId, String comment) {
        Admin admin = adminService.getCurrentUser();
        log.info("调用拒绝打款接口,入参cashId:{},操作人:{}", cashId, admin.getId());
        CashDraws cashDraws = findById(cashId);
        if (null == cashDraws) {
            return false;
        }
        cashDraws.setOperator(admin.getName());
        cashDraws.setComment(comment);
        //修改为"已拒绝"状态
        cashDraws.setCashStatus(CashProcessStatusEnum.REFUSE.getType());
        cashDraws.setProcessTime(new Date());
        cashDrawsDao.update(cashDraws);

        //返款
        User user = userService.findById(cashDraws.getUserId());
        BigDecimal balance = user.getBalance();
        log.info("管理员拒绝打款前，用户账户余额:{}", balance);
        balance = balance.add(cashDraws.getMoney());
        log.info("管理员拒绝打款后，用户账户余额:{}", balance);

        MoneyDetails moneyDetails = new MoneyDetails();
        moneyDetails.setOperatorId(admin.getId());
        moneyDetails.setAction(MoneyOperateTypeEnum.ADMIN_REFUSE_REMIT.getType());
        moneyDetails.setTargetId(cashDraws.getUserId());
        moneyDetails.setSum(balance);
        moneyDetails.setMoney(cashDraws.getMoney());
        moneyDetails.setCashId(cashId);
        moneyDetails.setRemark(comment);
        moneyDetails.setCreateTime(new Date());
        mdService.create(moneyDetails);

        user.setBalance(balance);
        user.setUpdateTime(new Date());
        userService.update(user);
        return true;
    }

    @Override
    public CashDrawsVO withdrawCharm(Integer charm) {
        User user = userService.getCurrentUser();

        boolean result = userBodyAuthService.userAlreadyAuth(user.getId());
        if (!result) {
            log.error("用户id：{}未进行用户身份实名认证", user.getId());
            throw new UserException(UserException.ExceptionCode.BODY_NO_AUTH);
        }

        Integer totalCharm = user.getCharm() == null ? 0 : user.getCharm();
        if (totalCharm.compareTo(charm) < 0) {
            log.error("用户id：{}魅力值提现异常，总魅力值：{}，提现魅力值：{}", user.getId(), totalCharm, charm);
            throw new CashException(CashException.ExceptionCode.CASH_EXCEED_EXCEPTION);
        }

        user.setTotalWithdrawCharm((user.getTotalWithdrawCharm() == null ? 0 : user.getTotalWithdrawCharm()) + charm);
        user.setUpdateTime(DateUtil.date());
        userService.update(user);

        BigDecimal charmMoney = new BigDecimal(charm).multiply(Constant.CHARM_TO_MONEY_RATE);

        CashDraws cashDraws = new CashDraws();
        cashDraws.setUserId(user.getId());
        cashDraws.setNickname(user.getNickname());
        cashDraws.setMobile(user.getMobile());
        cashDraws.setMoney(charmMoney);
        cashDraws.setCashStatus(CashProcessStatusEnum.WAITING.getType());
        cashDraws.setServerAuth(CashDrawsServerAuthEnum.UN_PROCESS.getType());
        cashDraws.setType(CashDrawsTypeEnum.CHARM_WITHDRAW.getType());
        cashDraws.setCashNo(generateCashNo());
        cashDraws.setCreateTime(DateUtil.date());
        cashDrawsDao.create(cashDraws);

        MoneyDetails details = new MoneyDetails();
        details.setOperatorId(user.getId());
        details.setTargetId(user.getId());
        details.setMoney(charmMoney);
        details.setAction(MoneyOperateTypeEnum.USER_DRAW_CASH.getType());
        details.setCashId(cashDraws.getCashId());
        details.setSum(new BigDecimal(user.getTotalWithdrawCharm()));
        details.setCreateTime(DateUtil.date());
        mdService.drawSave(details);

        CashDrawsVO vo = new CashDrawsVO();
        BeanUtil.copyProperties(cashDraws, vo);
        return vo;
    }


    /**
     * 生成订单号
     *
     * @return
     */
    private String generateCashNo() {
        String cashNo = GenIdUtil.GetOrderNo();
        if (findByCashNo(cashNo) == null) {
            return cashNo;
        } else {
            return generateCashNo();
        }
    }

    public CashDraws findByCashNo(String cashNo) {
        CashDrawsVO vo = new CashDrawsVO();
        vo.setCashNo(cashNo);
        List<CashDraws> list = cashDrawsDao.findByParameter(vo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}
