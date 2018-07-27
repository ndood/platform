package com.fulu.game.point.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.PointTypeEnum;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.common.enums.UserStatusEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.SystemException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.OrderPointProductTO;
import com.fulu.game.core.entity.vo.OrderPointProductVO;
import com.fulu.game.core.entity.vo.PointOrderDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.point.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/order")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;
    @Autowired
    private GradingPriceService gradingPriceService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    /**
     * 抢单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list")
    public Result list(Integer pageNum,Integer pageSize){
        PageInfo<PointOrderDetailsVO> pageInfo = orderService.listPointOrderDetails(pageNum,pageSize);
        return Result.success().data(pageInfo);
    }


    /**
     * 段位价格计算
     * @param orderPointProductTO
     * @return
     */
    @RequestMapping(value = "/advance")
    public Result accurateSubmit(@Valid OrderPointProductTO orderPointProductTO) {
        OrderPointProductVO gradingAdvanceOrderVO = getAdvanceOrder(orderPointProductTO);
        return Result.success().data(gradingAdvanceOrderVO);
    }

    /**
     * 上分订单抢单
     * @param orderNo
     * @return
     */
    @PostMapping(value = "receive")
    public Result orderReceive(@RequestParam(required = true)String orderNo){
        Order order = orderService.findByOrderNo(orderNo);
        if(order==null){
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST,orderNo);
        }
        User user = userService.findById(userService.getCurrentUser().getId());
        if (!UserInfoAuthStatusEnum.VERIFIED.getType().equals(user.getUserInfoAuth()) || !UserStatusEnum.NORMAL.getType().equals(user.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_USER_NOT_VERIFIED,order.getOrderNo());
        }
        List<Integer> techAuthList = userTechAuthService.findUserNormalCategoryIds(user.getId());
        if(!techAuthList.contains(order.getCategoryId())){
            throw new OrderException(OrderException.ExceptionCode.ORDER_USER_NOT_HAS_TECH,order.getOrderNo());
        }
        orderService.receivePointOrder(order.getOrderNo(),user);
        return Result.success().data(orderNo).msg("接单成功!");
    }

    /**
     * 提交上分订单
     * @param request
     * @param orderPointProductTO
     * @param sessionkey
     * @return
     */
    @RequestMapping(value = "/submit")
    public Result submit(HttpServletRequest request,
                         @Valid OrderPointProductTO orderPointProductTO,
                         @RequestParam(required = true) String sessionkey) {
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:orderPointProductTO:{};sessionkey:{};",orderPointProductTO,sessionkey);
            throw new SystemException(SystemException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        OrderPointProductVO orderPointProductVO = getAdvanceOrder(orderPointProductTO);
        String orderIp = RequestUtil.getIpAdrress(request);
        String orderNo = orderService.submitPointOrder(orderPointProductVO,
                orderPointProductTO.getCouponNo(),
                orderPointProductTO.getContactType(),
                orderPointProductTO.getContactInfo(),orderIp);
        return Result.success().data(orderNo);
    }







    private OrderPointProductVO getAdvanceOrder(OrderPointProductTO orderPointProductTO) {
        OrderPointProductVO gradingAdvanceOrderVO = new OrderPointProductVO();
        gradingAdvanceOrderVO.setCategoryId(orderPointProductTO.getCategoryId());
        gradingAdvanceOrderVO.setPointType(orderPointProductTO.getPointType());
        gradingAdvanceOrderVO.setAreaId(orderPointProductTO.getAreaId());
        gradingAdvanceOrderVO.setGradingPriceId(orderPointProductTO.getGradingPriceId());
        gradingAdvanceOrderVO.setTargetGradingPriceId(orderPointProductTO.getTargetGradingPriceId());
        if (PointTypeEnum.ACCURATE_SCORE.getType().equals(orderPointProductTO.getPointType())) {
            //查询区间价格
            BigDecimal totalMoney = gradingPriceService.findRangePrice(gradingAdvanceOrderVO.getCategoryId(), gradingAdvanceOrderVO.getGradingPriceId(), gradingAdvanceOrderVO.getTargetGradingPriceId());
            gradingAdvanceOrderVO.setTotalMoney(totalMoney);
            //大区信息
            TechValue techValue = techValueService.findById(gradingAdvanceOrderVO.getAreaId());
            //账户信息
            GradingPrice startGradingPrice = gradingPriceService.findById(gradingAdvanceOrderVO.getGradingPriceId());
            GradingPrice parentStartGradingPrice = gradingPriceService.findById(startGradingPrice.getPid());
            gradingAdvanceOrderVO.setAccountInfo(techValue.getName() + "-" + parentStartGradingPrice.getName() + startGradingPrice.getName());
            //下单选择
            GradingPrice endGradingPrice = gradingPriceService.findById(gradingAdvanceOrderVO.getTargetGradingPriceId());
            GradingPrice parentEndGradingPrice = gradingPriceService.findById(endGradingPrice.getPid());
            gradingAdvanceOrderVO.setOrderChoice(parentStartGradingPrice.getName() + startGradingPrice.getName() + "-" + parentEndGradingPrice.getName() + endGradingPrice.getName());

            gradingAdvanceOrderVO.setPrice(totalMoney);
            gradingAdvanceOrderVO.setAmount(1);

        } else {
            //todo 包赢和开黑逻辑

        }
        gradingAdvanceOrderVO.setPointTypeStr(PointTypeEnum.getMsgByType(gradingAdvanceOrderVO.getPointType()));
        Category category = categoryService.findById(orderPointProductTO.getCategoryId());
        gradingAdvanceOrderVO.setCategoryName(category.getName());
        return gradingAdvanceOrderVO;
    }

}
