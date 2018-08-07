import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.impl.FenqileOrderServiceImpl;

import java.math.BigDecimal;
import java.util.Date;

public class FenqileOrderTest {

    public static void main(String[] args) {
        FenqileOrderServiceImpl orderService = new FenqileOrderServiceImpl();
        FenqileConfig fenqileConfig = new FenqileConfig();
        fenqileConfig.setPartnerId("1005414");
        fenqileConfig.setPartnerKey("ddsad");
        fenqileConfig.setV("1.4");
        orderService.setConfig(fenqileConfig);

        FenqileOrderRequest fenqileOrderRequest = new FenqileOrderRequest();
        fenqileOrderRequest.setSkuId("MES201710311458265");
        fenqileOrderRequest.setThirdUid("ffda99d8a8305eaed672c3f47f0eb64a88c5576d");
        fenqileOrderRequest.setAmount(new BigDecimal("22.22"));
        fenqileOrderRequest.setThirdOrderId("20171031183837528");
        fenqileOrderRequest.setCreateTime(new Date());
        fenqileOrderRequest.setSubject("lalal");


        orderService.createOrder(fenqileOrderRequest,Object.class);

    }

}
