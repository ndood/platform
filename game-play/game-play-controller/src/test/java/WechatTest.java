import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.service.PayService;
import com.fulu.game.play.PlayApplication;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class WechatTest {

    @Autowired
    private  WxPayService wxPayService;
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private PayService payService;

    @Test
    public void test1(){
        SMSUtil.sendOrderReceivingRemind("18801285391","230024144");
    }

    @Test
    public void test2() {
        try {
            WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
            wxMaTemplateMessage.setTemplateId("yD7JulFzNv7ZNInswmn6_hdgwlf68qRL0fwLUNq98Vc");
            wxMaTemplateMessage.setToUser("oZKvq4hk9VKOi4-khODa1FSfA7GA");
            WxMaTemplateMessage.Data d1 = new WxMaTemplateMessage.Data("keyword1", "LAL");
            WxMaTemplateMessage.Data d2 = new WxMaTemplateMessage.Data("keyword2", "HEEL");
            List<WxMaTemplateMessage.Data> dataList = new ArrayList<>();
            dataList.add(d1);
            dataList.add(d2);
            wxMaTemplateMessage.setData(dataList);
            wxMaTemplateMessage.setFormId("form1");
            wxMaService.getMsgService().sendTemplateMsg(wxMaTemplateMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
