package com.fulu.game.core.service;

import com.fulu.game.core.entity.AppstorePayDetail;



/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-19 20:45:00
 */
public interface AppstorePayDetailService extends ICommonService<AppstorePayDetail,Integer>{


    AppstorePayDetail findByTransactionId(String transactionId);
}
