import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.fulu.game.common.utils.HttpUtils;
import com.fulu.game.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序二维码测试类
 * 选择接口B，生成数量无限制
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
public class WxaCodeTest {
    @Autowired
    private WxMaService wxMaService;

    @Test
    public void testCode1() throws WxErrorException {
        File file = wxMaService.getQrcodeService().createWxCodeLimit("id=1", "pages/index/index");
    }

    @Test
    public void testCode() {
        String token = getAccessTokenTest();
        getWxaCode(token);
    }

    /**
     * 获取accessToken
     */
    public String getAccessTokenTest() {

        String appId = "wx27fc4b9385ffde13";
        String secret = "40ef671d910e97b55d4370031af1d9c2";
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                + "&appid=" + appId
                + "&secret=" + secret;
        String jsonResult = HttpUtils.get(tokenUrl, null);
        //预期返回：{"access_token":"ACCESS_TOKEN","expires_in":7200}
        //错误返回：{"errcode":40013,"errmsg":"invalid appid"}
        JSONObject jo = new JSONObject(jsonResult);
        if (jo.containsKey("access_token")) {
            return jo.getStr("access_token");
        } else {
            return null;
        }
    }

    /**
     * 获取小程序码
     * 结果为二进制输入流
     *
     * @param accessToken
     */
    public void getWxaCode(String accessToken) {
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
        String property = getCodeProperty();
        InputStream in = HttpRequest.post(url)
                .body(property)
                .execute()
                .bodyStream();
    }

    /**
     * 小程序码属性参数
     *
     * @return
     */
    public String getCodeProperty() {
        Map<String, Object> line_color = new HashMap<>();
        line_color.put("r", 0);
        line_color.put("g", 0);
        line_color.put("b", 0);
        Map<String, Object> paramMap = new HashMap<>();
        //paramMap.put("scene", "");
        paramMap.put("page", "pages/c/card/card");
        paramMap.put("width", "430");
        paramMap.put("auto_color", false);
        paramMap.put("line_color", line_color);
        //paramMap.put("is_hyaline", false);
        JSONObject jo = new JSONObject(paramMap);
        log.info("调用小程序二维码获取接口参数：{}", jo);
        return jo.toString();
    }

}
