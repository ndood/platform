package com.fulu.game.point.controller;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.OrderPointProductTO;
import com.fulu.game.core.entity.vo.OrderEventVO;
import com.fulu.game.core.entity.vo.OrderPointProductVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.PointOrderDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import com.fulu.game.point.service.impl.PointMiniAppPayServiceImpl;
import com.fulu.game.point.service.impl.PointMiniAppOrderServiceImpl;
import com.fulu.game.point.service.impl.PointMiniAppPushServiceImpl;
import com.fulu.game.point.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/order")
public class OrderController extends BaseController {

    @Autowired
    private PointMiniAppOrderServiceImpl pointMiniAppOrderServiceImpl;
    @Autowired
    private GradingPriceService gradingPriceService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private UserService userService;
    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private PointMiniAppPayServiceImpl pointMiniAppPayService;
    @Autowired
    private OrderPointProductService orderPointProductService;
    @Autowired
    private PointMiniAppPushServiceImpl pointMiniAppPushService;

    /**
     * 订单详情
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/details")
    public Result orderDetails(@RequestParam(required = true) String orderNo) {
        PointOrderDetailsVO orderDetailsVO = pointMiniAppOrderServiceImpl.findPointOrderDetails(orderNo);
        return Result.success().data(orderDetailsVO);
    }

    /**
     * 订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param type
     * @return
     */
    @RequestMapping(value = "/list")
    public Result orderList(Integer pageNum,
                            Integer pageSize,
                            Integer type) {
        PageInfo<PointOrderDetailsVO> pageInfo = pointMiniAppOrderServiceImpl.pointOrderList(pageNum, pageSize, type);
        return Result.success().data(pageInfo);
    }


    /**
     * 新订单数
     *
     * @param startDate
     * @return
     */
    @RequestMapping(value = "/new-order-count")
    public Result newOrderCount(Date startDate) {
        Integer count = pointMiniAppOrderServiceImpl.countNewPointOrder(startDate);
        return Result.success().data(count);
    }

    /**
     * 抢单列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/receive/list")
    public Result list(@RequestParam(value = "pageNum") Integer pageNum,
                       @RequestParam(value = "pageSize") Integer pageSize) {
        PageInfo<PointOrderDetailsVO> pageInfo = pointMiniAppOrderServiceImpl.receivingPointOrderList(pageNum, pageSize);
        return Result.success().data(pageInfo);
    }


    /**
     * 段位价格计算
     *
     * @param orderPointProductTO
     * @return
     */
    @RequestMapping(value = "/advance")
    public Result accurateSubmit(@Valid OrderPointProductTO orderPointProductTO) {
        OrderPointProductVO gradingAdvanceOrderVO = getAdvanceOrder(orderPointProductTO);
        return Result.success().data(gradingAdvanceOrderVO);
    }

    /**
     * 重新下单接口
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/anew")
    public Result anewSubmit(String orderNo) {
        OrderPointProduct orderPointProduct = orderPointProductService.findByOrderNo(orderNo);
        OrderPointProductTO orderPointProductTO = new OrderPointProductTO();
        BeanUtil.copyProperties(orderPointProduct, orderPointProductTO);
        OrderPointProductVO gradingAdvanceOrderVO = getAdvanceOrder(orderPointProductTO);
        return Result.success().data(gradingAdvanceOrderVO);
    }


    /**
     * 上分订单抢单
     *
     * @param orderNo 订单编号
     * @return 封装结果集
     */
    @PostMapping(value = "/receive")
    public Result orderReceive(@RequestParam(required = true) String orderNo) {
        Order order = pointMiniAppOrderServiceImpl.findByOrderNo(orderNo);
        if (order == null) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST, orderNo);
        }
        User user = userService.findById(userService.getCurrentUser().getId());
        if (!UserInfoAuthStatusEnum.VERIFIED.getType().equals(user.getUserInfoAuth()) || !UserStatusEnum.NORMAL.getType().equals(user.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_USER_NOT_VERIFIED, order.getOrderNo());
        }
        List<Integer> techAuthList = userTechAuthService.findUserNormalCategoryIds(user.getId());
        if (!techAuthList.contains(order.getCategoryId())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_USER_NOT_HAS_TECH, order.getOrderNo());
        }
        pointMiniAppOrderServiceImpl.receivePointOrder(order.getOrderNo(), user);
        return Result.success().data(orderNo).msg("接单成功!");
    }


    /**
     * 提交上分订单
     *
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
            log.error("验证sessionkey错误:orderPointProductTO:{};sessionkey:{};", orderPointProductTO, sessionkey);
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        OrderPointProductVO orderPointProductVO = getAdvanceOrder(orderPointProductTO);
        String orderIp = RequestUtil.getIpAdrress(request);
        String orderNo = pointMiniAppOrderServiceImpl.submitPointOrder(orderPointProductVO,
                orderPointProductTO.getCouponNo(),
                orderPointProductTO.getContactType(),
                orderPointProductTO.getContactInfo(), orderIp);
        return Result.success().data(orderNo);
    }


    /**
     * 订单支付接口
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/pay")
    @Deprecated
    public Result pay(@RequestParam(required = true) String orderNo,
                      HttpServletRequest request) {
        String ip = RequestUtil.getIpAdrress(request);
        Order order = pointMiniAppOrderServiceImpl.findByOrderNo(orderNo);
        User user = userService.findById(order.getUserId());
        Object result = pointMiniAppPayService.payOrder(order, user, ip);
        return Result.success().data(result);
    }

    /**
     * 用户取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/cancel")
    public Result userCancelOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = pointMiniAppOrderServiceImpl.userCancelOrder(orderNo);
        return Result.success().data(orderVO).msg("取消订单成功!");
    }


    /**
     * 申请客服仲裁
     *
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @RequestMapping(value = "/user/appeal")
    public Result userAppealOrder(@RequestParam(required = true) String orderNo,
                                  String remark,
                                  @RequestParam(required = true) String[] fileUrl) {
        pointMiniAppOrderServiceImpl.userAppealOrder(orderNo, remark, fileUrl);
        return Result.success().data(orderNo).msg("订单申诉成功!");
    }


    /**
     * 提醒开始服务
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/remind/start-order")
    public Result remindStartOrder(@RequestParam(required = true) String orderNo) {
        if (redisOpenService.isTimeIntervalInside(orderNo)) {
            return Result.error().msg("不能频繁提醒开始!");
        }
        Order order = pointMiniAppOrderServiceImpl.findByOrderNo(orderNo);
        //发送通知
        pointMiniAppPushService.remindReceive(order);
        redisOpenService.setTimeInterval(orderNo, 5 * 60);
        return Result.success().msg("提醒开始成功!");
    }

    /**
     * 用户协商订单
     *
     * @return
     */
    @RequestMapping(value = "/user/consult")
    public Result consult(@RequestParam(required = true) String orderNo,
                          BigDecimal refundMoney,
                          String remark,
                          @RequestParam(required = true) String[] fileUrl) {

        if (redisOpenService.isTimeIntervalInside(OrderEventService.CANCEL_CONSULT_LIMIT + orderNo)) {
            return Result.error().msg("取消协商后,两个小时内不能重复协商!");
        }
        pointMiniAppOrderServiceImpl.userConsultOrder(orderNo, refundMoney, remark, fileUrl);
        return Result.success().data(orderNo);
    }


    /**
     * 陪玩师同意协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/server/consult-appeal")
    public Result consultAppeal(@RequestParam(required = true) String orderNo,
                                Integer orderEventId) {
        pointMiniAppOrderServiceImpl.consultAgreeOrder(orderNo, orderEventId);
        return Result.success().data(orderNo);
    }


    /**
     * 陪玩师拒绝协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/server/consult-reject")
    public Result consultReject(@RequestParam(required = true) String orderNo,
                                Integer orderEventId,
                                String remark,
                                @RequestParam(required = true) String[] fileUrl) {
        pointMiniAppOrderServiceImpl.consultRejectOrder(orderNo, orderEventId, remark, fileUrl);
        return Result.success().data(orderNo);
    }

    /**
     * 用户取消协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/user/consult-cancel")
    public Result consultCancel(@RequestParam(required = true) String orderNo,
                                Integer orderEventId) {
        pointMiniAppOrderServiceImpl.consultCancelOrder(orderNo, orderEventId);
        return Result.success().data(orderNo).msg("取消协商成功!");
    }


    /**
     * 陪玩师开始服务
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/start-serve")
    public Result startServerOrder(@RequestParam(required = true) String orderNo) {
        pointMiniAppOrderServiceImpl.serverStartServeOrder(orderNo);
        return Result.success().data(orderNo).msg("接单成功!");
    }


    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/verify")
    public Result userVerifyOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = pointMiniAppOrderServiceImpl.userVerifyOrder(orderNo);
        return Result.success().data(orderVO).msg("订单验收成功!");
    }


    /**
     * 陪玩师取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/cancel")
    public Result serverCancelOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = pointMiniAppOrderServiceImpl.serverCancelOrder(orderNo);
        return Result.success().data(orderVO).msg("取消订单成功!");
    }

    /**
     * 陪玩师提交验收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/acceptance")
    public Result serverAcceptanceOrder(@RequestParam(required = true) String orderNo,
                                        String remark,
                                        String[] fileUrl) {
        OrderVO orderVO = pointMiniAppOrderServiceImpl.serverAcceptanceOrder(orderNo, remark, fileUrl);
        return Result.success().data(orderVO).msg("提交订单验收成功!");
    }


    /**
     * 订单事件查询（查询协商和申诉）
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/event")
    public Result orderEvent(@RequestParam(required = true) String orderNo) {
        OrderEventVO orderEventVO = pointMiniAppOrderServiceImpl.findOrderEvent(orderNo);
        return Result.success().data(orderEventVO);
    }

    /**
     * 留言接口
     *
     * @param orderNo
     * @param orderEventId
     * @param remark
     * @param fileUrl
     * @return
     */
    @RequestMapping(value = "/leave-msg")
    public Result orderLeaveMsg(@RequestParam(required = true) String orderNo,
                                Integer orderEventId,
                                String remark,
                                String[] fileUrl) {
        OrderDeal orderDeal = pointMiniAppOrderServiceImpl.eventLeaveMessage(orderNo, orderEventId, remark, fileUrl);
        return Result.success().data(orderDeal);
    }

    private OrderPointProductVO getAdvanceOrder(OrderPointProductTO orderPointProductTO) {
        log.info("开始计算段位区间价格:orderPointProductTO:{}", orderPointProductTO);
        OrderPointProductVO gradingAdvanceOrderVO = new OrderPointProductVO();
        gradingAdvanceOrderVO.setCategoryId(orderPointProductTO.getCategoryId());
        gradingAdvanceOrderVO.setPointType(orderPointProductTO.getPointType());
        gradingAdvanceOrderVO.setAreaId(orderPointProductTO.getAreaId());
        gradingAdvanceOrderVO.setGradingPriceId(orderPointProductTO.getGradingPriceId());
        gradingAdvanceOrderVO.setTargetGradingPriceId(orderPointProductTO.getTargetGradingPriceId());
        if (PointTypeEnum.ACCURATE_SCORE.getType().equals(orderPointProductTO.getPointType())) {
            TechValue techValue = techValueService.findById(gradingAdvanceOrderVO.getAreaId());
            //账户信息
            GradingPrice startGradingPrice = gradingPriceService.findById(gradingAdvanceOrderVO.getGradingPriceId());
            GradingPrice parentStartGradingPrice = gradingPriceService.findById(startGradingPrice.getPid());
            gradingAdvanceOrderVO.setAccountInfo(techValue.getName() + "-" + parentStartGradingPrice.getName() + " " + startGradingPrice.getName());
            //下单选择
            GradingPrice endGradingPrice = gradingPriceService.findById(gradingAdvanceOrderVO.getTargetGradingPriceId());
            GradingPrice parentEndGradingPrice = gradingPriceService.findById(endGradingPrice.getPid());
            gradingAdvanceOrderVO.setOrderChoice(parentStartGradingPrice.getName() + " " + startGradingPrice.getName() + "-" + parentEndGradingPrice.getName() + " " + endGradingPrice.getName());
            //查询区间价格
            BigDecimal totalMoney = gradingPriceService.findRangePrice(gradingAdvanceOrderVO.getCategoryId(), gradingAdvanceOrderVO.getGradingPriceId(), gradingAdvanceOrderVO.getTargetGradingPriceId());
            totalMoney = totalMoney.subtract(endGradingPrice.getPrice());
            gradingAdvanceOrderVO.setTotalMoney(totalMoney);
            //计算段位区间价格
            gradingAdvanceOrderVO.setPrice(totalMoney);
            gradingAdvanceOrderVO.setAmount(1);

        } else {
            //todo 包赢和开黑逻辑

        }
        gradingAdvanceOrderVO.setPointTypeStr(PointTypeEnum.getMsgByType(gradingAdvanceOrderVO.getPointType()));
        Category category = categoryService.findById(orderPointProductTO.getCategoryId());
        gradingAdvanceOrderVO.setCategoryName(category.getName());
        gradingAdvanceOrderVO.setCategoryIcon(category.getIcon());
        return gradingAdvanceOrderVO;
    }

    /**
     * 获取是否有陪玩师接单状态
     *
     * @return 封装结果集
     */
    @PostMapping("/accept-order/status")
    public DeferredResult<Result> getServiceUserAcceptOrderStatus() {
        DeferredResult<Result> resultDeferredResult = pointMiniAppOrderServiceImpl.getServiceUserAcceptOrderStatus();
        return resultDeferredResult;
    }

}
