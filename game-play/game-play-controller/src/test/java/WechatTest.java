import com.fulu.game.play.PlayApplication;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class WechatTest {

    @Autowired
    private  WxPayService wxPayService;

    @Test
    public void test1(){
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody("主题");
        orderRequest.setOutTradeNo("123457474741");
        orderRequest.setTotalFee(1);//元转成分
        orderRequest.setOpenid("oZKvq4g36ualRRgTOpDq6RXJ8oig");
        orderRequest.setSpbillCreateIp("10.25.14.1");
        orderRequest.setTimeStart(DateUtil.format(new Date(),"yyyyMMddHHmmss"));
        try {
            WxPayUnifiedOrderResult wxPayUnifiedOrderResult = wxPayService.unifiedOrder(orderRequest);
            System.out.println(wxPayUnifiedOrderResult);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
