package com.fulu.game.core.service.impl.payment;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.core.entity.FenqileOrder;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayCallbackRes;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.service.FenqileOrderService;
import com.fulu.game.core.service.ThirdpartyUserService;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderNotice;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.FenqileSdkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class FenqilePaymentComponent implements PaymentComponent {

    @Autowired
    private ThirdpartyUserService thirdpartyUserService;
    @Autowired
    private FenqileSdkOrderService fenqileSdkOrderService;
    @Autowired
    private FenqileOrderService fenqileOrderService;

    @Override
    public PayRequestRes payRequest(PayRequestModel payRequestModel) {
        String result = "";
        if (payRequestModel.getPayBusinessEnum().equals(PayBusinessEnum.ORDER)) {
            Order order = payRequestModel.getOrder();
            User user = payRequestModel.getUser();
            ThirdpartyUser thirdpartyUser = thirdpartyUserService.findByUserId(user.getId());
            FenqileOrderRequest fenqileOrderRequest = new FenqileOrderRequest();
            fenqileOrderRequest.setSubject(order.getName());
            fenqileOrderRequest.setThirdOrderId(order.getOrderNo());
            fenqileOrderRequest.setSkuId("MES201809252323331");
            fenqileOrderRequest.setThirdUid(thirdpartyUser.getFqlOpenid());
            fenqileOrderRequest.setAmount(order.getActualMoney());
            fenqileOrderRequest.setCreateTime(DateUtil.now());
            result = fenqileSdkOrderService.createOrder(fenqileOrderRequest);
        }
        PayRequestRes payRequestRes = new PayRequestRes(false);
        payRequestRes.setRequestParameter(result);
        return payRequestRes;
    }

    @Override
    public PayCallbackRes payCallBack(PayCallbackModel payCallbackVO) {
        FenqileOrderNotice fenqileOrderNotice = payCallbackVO.getFenqileOrderNotice();
        // 结果正确
        String orderNo = fenqileOrderNotice.getThirdOrderId();
        String totalYuan = fenqileOrderNotice.getAmount().toPlainString();
        PayCallbackRes payCallbackTO = new PayCallbackRes();
        boolean flag = false;
        if (Integer.valueOf(12).equals(fenqileOrderNotice.getMerchSaleState())) {
            flag = true;
        }
        payCallbackTO.setSuccess(flag);
        payCallbackTO.setOrderNO(orderNo);
        payCallbackTO.setPayMoney(totalYuan);
        return payCallbackTO;
    }

    @Override
    public boolean refund(RefundModel refundModel) {
        FenqileOrder fenqileOrder = fenqileOrderService.findByOrderNo(refundModel.getOrderNo());
        String orderNo = refundModel.getOrderNo();
        if (fenqileOrder == null) {
            throw new PayException(PayException.ExceptionCode.THIRD_REFUND_FAIL, orderNo);
        }
        if (refundModel.getTotalMoney().equals(refundModel.getRefundMoney())) {
            log.info("调用分期乐全部退款:fenqileOrder:{},totalMoney:{},refundMoney:{}", refundModel.getOrderNo(), refundModel.getTotalMoney(), refundModel.getRefundMoney());
            return fenqileSdkOrderService.cancelFenqileOrder(refundModel.getOrderNo(), fenqileOrder.getFenqileNo());
        } else {
            log.info("调用分期乐部分退款:fenqileOrder:{},totalMoney:{},refundMoney:{}", fenqileOrder, refundModel.getTotalMoney(), refundModel.getRefundMoney());
            return fenqileSdkOrderService.noticeFenqileRefund(refundModel.getOrderNo(), fenqileOrder.getFenqileNo(),  refundModel.getRefundMoney());
        }
    }
}
