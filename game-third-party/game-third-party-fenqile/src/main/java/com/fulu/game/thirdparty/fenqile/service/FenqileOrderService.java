package com.fulu.game.thirdparty.fenqile.service;

import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;

public interface FenqileOrderService {


    public  <T> T createOrder(FenqileOrderRequest fenqileOrderRequest);
}
