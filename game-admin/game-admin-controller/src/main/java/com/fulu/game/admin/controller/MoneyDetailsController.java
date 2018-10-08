package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/api/v1/moneyDetails")
public class MoneyDetailsController extends BaseController{
    @Autowired
    private MoneyDetailsService moneyDetailsService;

    @Autowired
    private UserService userService;

    /**
     * 管理员-查询-加零钱列表
     *
     * @param moneyDetailsVO
     * @param pageSize
     * @param pageNum
     * @return
     */
    @PostMapping("/list")
    public Result list(MoneyDetailsVO moneyDetailsVO,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNum") Integer pageNum) {
        PageInfo<MoneyDetailsVO> list = moneyDetailsService.listByAdmin(moneyDetailsVO, pageSize, pageNum);
        return Result.success().data(list).msg("查询列表成功！");
    }

    /**
     * 管理员-加零钱
     *
     * @param moneyDetailsVO
     * @return
     */
    @PostMapping("/save")
    public Result save(MoneyDetailsVO moneyDetailsVO) {
        if (moneyDetailsVO.getMoney().compareTo(BigDecimal.ZERO) == -1) {
            throw new CashException(CashException.ExceptionCode.CASH_NEGATIVE_EXCEPTION);
        }
        User user = userService.findByMobile(moneyDetailsVO.getMobile());
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        if (StringUtils.isEmpty(user.getOpenId())) {
            return Result.error().msg("该用户尚未绑定微信！");
        }
        MoneyDetails moneyDetails = moneyDetailsService.save(moneyDetailsVO);
        return Result.success().data(moneyDetails).msg("操作成功！");
    }

    /**
     * 管理员-扣零钱
     *
     * @param moneyDetailsVO
     * @return
     */
    @PostMapping("/subtract-save")
    public Result subtractSave(MoneyDetailsVO moneyDetailsVO) {
        if(moneyDetailsVO == null){
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        } else {
            moneyDetailsVO.setAction(MoneyOperateTypeEnum.ADMIN_SUBTRACT_CHANGE.getType());
        }
        return save(moneyDetailsVO);
    }

    /**
     * 用户账户明细
     *
     * @return 列表
     */
    @PostMapping("/user")
    public Result listUserDetails(MoneyDetailsVO moneyDetailsVO,
                                  @RequestParam("pageNum") Integer pageNum,
                                  @RequestParam("pageSize") Integer pageSize) {
        if (moneyDetailsVO.getTargetId() == null) {
            return Result.error().msg("用户id参数为空");
        }
        PageInfo<MoneyDetails> list = moneyDetailsService.listUserDetails(moneyDetailsVO, pageNum, pageSize);
        return Result.success().data(list);
    }
}
