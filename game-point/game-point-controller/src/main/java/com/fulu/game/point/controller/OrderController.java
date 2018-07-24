package com.fulu.game.point.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.TOWinTypeEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.GradingPrice;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.to.OrderPointProductTO;
import com.fulu.game.core.entity.vo.OrderPointProductVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.GradingPriceService;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.TechValueService;
import com.fulu.game.point.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
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


    @RequestMapping(value = "/advance")
    public Result accurateSubmit(@Valid OrderPointProductTO orderPointProductTO) {
        OrderPointProductVO gradingAdvanceOrderVO = getAdvanceOrder(orderPointProductTO);
        return Result.success().data(gradingAdvanceOrderVO);
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
        if (TOWinTypeEnum.ACCURATE_SCORE.getType().equals(orderPointProductTO.getPointType())) {
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

        } else {
            //todo 包赢和开黑逻辑


        }
        gradingAdvanceOrderVO.setPointTypeStr(TOWinTypeEnum.getMsgByType(gradingAdvanceOrderVO.getPointType()));
        Category category = categoryService.findById(orderPointProductTO.getCategoryId());
        gradingAdvanceOrderVO.setCategoryName(category.getName());
        return gradingAdvanceOrderVO;
    }

}
