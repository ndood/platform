package com.fulu.game.core.service.impl.push;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MobileAppPushServiceImpl extends PushServiceImpl implements IBusinessPushService{



    public void pushMsg(AppPushMsgVO appPushMsgVO){
        if(appPushMsgVO.getSendAll()){
            pushMsg(appPushMsgVO.getTitle(),appPushMsgVO.getAlert(),appPushMsgVO.getExtras());
        }else {
            pushMsg(appPushMsgVO.getTitle(),appPushMsgVO.getAlert(),appPushMsgVO.getExtras(),appPushMsgVO.getUserIds());
        }
    }


    @Override
    public void receiveOrder(Order order) {

    }

    @Override
    public void remindReceive(Order order) {

    }

    @Override
    public void remindStart(Order order) {

    }

    @Override
    public void serviceUserAcceptOrder(Order order) {

    }

    @Override
    public void start(Order order) {

    }

    @Override
    public void consult(Order order) {

    }

    @Override
    public void rejectConsult(Order order) {

    }

    @Override
    public void agreeConsult(Order order) {

    }

    @Override
    public void cancelConsult(Order order) {

    }

    @Override
    public void cancelOrderByServer(Order order) {

    }

    @Override
    public void cancelOrderByUser(Order order) {

    }

    @Override
    public void appealByServer(Order order) {

    }

    @Override
    public void appealByUser(Order order) {

    }

    @Override
    public void checkOrder(Order order) {

    }

    @Override
    public void orderPay(Order order) {

    }

    @Override
    public void acceptOrder(Order order) {

    }

    @Override
    public void grantCouponMsg(int userId, String deduction) {

    }
}
