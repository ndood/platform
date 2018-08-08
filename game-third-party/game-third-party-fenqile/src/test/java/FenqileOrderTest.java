import cn.hutool.core.date.DateUtil;
import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.impl.FenqileOrderServiceImpl;
import com.fulu.game.thirdparty.fenqile.util.SignUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FenqileOrderTest {


    public static void main(String[] args) {
        //timestamp=1514446696&partner_id=PAI201605240000011&format=json&v=1.4&method=fenqile.third.notice.modify
//        String partner_key = "f5d764053cd418601131c30da134428b";
//        Map<String,Object> map = new HashMap<>();
//        map.put("timestamp",1462533027);
//        map.put("partner_id","MPA201605060000023");
//        map.put("format","json");
//        map.put("v","1.3");
//        map.put("method","fenqile.trade.detail.get");
//        map.put("order_id","O20160113003075100345");
//
//
//        String sign = SignUtil.createSign(map,partner_key);
//        System.out.println(sign);

        test1();
    }


    public static void test1() {
        FenqileOrderServiceImpl orderService = new FenqileOrderServiceImpl();
        FenqileConfig fenqileConfig = new FenqileConfig();
        fenqileConfig.setPartnerId("PAI201808080000195");
        fenqileConfig.setPartnerKey("43ea4b7f27e36f2eb7b194b3924291ca");
        fenqileConfig.setV("1.3");
        orderService.setConfig(fenqileConfig);

        FenqileOrderRequest fenqileOrderRequest = new FenqileOrderRequest();
        fenqileOrderRequest.setSkuId("MES201710311458265");
        fenqileOrderRequest.setThirdUid("ffda99d8a8305eaed672c3f47f0eb64a88c5576d");
        fenqileOrderRequest.setAmount(new BigDecimal("22.22"));
        fenqileOrderRequest.setThirdOrderId("20171031183837528");
        fenqileOrderRequest.setCreateTime(DateUtil.formatDateTime(new Date()));
        fenqileOrderRequest.setSubject("lalal");


        orderService.createOrder(fenqileOrderRequest,Map.class);

    }

}
