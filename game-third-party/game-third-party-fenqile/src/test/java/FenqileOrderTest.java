import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.impl.FenqileOrderServiceImpl;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FenqileOrderTest {


    public static void main(String[] args) {
//        timestamp=1514446696&partner_id=PAI201605240000011&format=json&v=1.4&method=fenqile.third.notice.modify
//        String partner_key = "f5d764053cd418601131c30da134428b";
//        Map<String,Object> map = new HashMap<>();
//        map.put("timestamp",1462533027);
//        map.put("partner_id","MPA201605060000023");
//        map.put("format","json");
//        map.put("v","1.3");
//        map.put("method","fenqile.trade.detail.get");
//        map.put("order_id","O20160113003075100345");

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
        fenqileConfig.setV("1.4");
        orderService.setConfig(fenqileConfig);
        orderService.noticeModify(1, "http://www.baidu.com");

    }

    @Test
    public void testFenqilePay(){
        FenqileOrderServiceImpl fenqileOrderService = new FenqileOrderServiceImpl();
        fenqileOrderService.setConfig(getFenqileConfig());
        FenqileOrderRequest fenqileOrderRequest = new FenqileOrderRequest();
        fenqileOrderRequest.setSubject("xxx");
        fenqileOrderRequest.setThirdOrderId("1474747474");
        fenqileOrderRequest.setSkuId("MES201809252323331");
        fenqileOrderRequest.setThirdUid("d5f13ace409b75d56f92a79e82b1cbef5e3d55db");
        fenqileOrderRequest.setAmount(new BigDecimal(20));
        fenqileOrderRequest.setCreateTime("2017-09-26 18:40:30");
        Object o=    fenqileOrderService.createOrder(fenqileOrderRequest);
        System.out.println(o);
    }

        @Test
    public void testNoticeModify(){
        FenqileOrderServiceImpl fenqileOrderService = new FenqileOrderServiceImpl();
        fenqileOrderService.setConfig(getFenqileConfig());

         fenqileOrderService.noticeModify(1,"https://t-api-h5.wzpeilian.com/fenqile/callback/order");
    }





    public FenqileConfig getFenqileConfig(){
        FenqileConfig fenqileConfig = new FenqileConfig();
        fenqileConfig.setPartnerId("PAI201808080000195");
        fenqileConfig.setPartnerKey("43ea4b7f27e36f2eb7b194b3924291ca");
        fenqileConfig.setSellerId("PMC20180807003400547501");
        fenqileConfig.setClientId("kaihei");
        fenqileConfig.setClientSecret("fe3a2eb4d6661dd36af8fb45f50ac8de");
        fenqileConfig.setV("1.4");
        return fenqileConfig;
    }

}
