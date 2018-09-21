import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import lombok.ToString;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PayTest2 {


    public final static String APP_ID = "2018091361378432";
    public final static String APP_PRIVATE_KEY =
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCu6Uf2OpJEEeJ+" +
                    "ydLZVErFsd+2RoqglP7KwDjSGParuS7sq/e9llR4aJzAvYd+hoc9WxCau3pLVYUr" +
                    "ofEVEyC8YQC4vT/U9MI/JbRfEs/KEFQAmF80rLxFMZChavQczhsEvyRhuo3R2pZH" +
                    "VmcDoqKpUhlDDxr1aPEG8pgxciPRZy2N3MweQg5mRVJhCm+jrv5povmBCgQemj5x" +
                    "TKWjF8KNXROKG2xaXs5XoGsj7sWVRbrgTkGzJ0tv1cCo2yAGIeErIPNJnkTWbili" +
                    "1suz14AZ6nOSzeBCBVHdEUC+HbrEogL3TkAdaSHwdK46lcvZmq3nFaCDac0yrYOD" +
                    "AnryDTKzAgMBAAECggEANJMUOVPjsSg7D2c0xHSMwR21I8HSW3BNob3jlmtAgd94" +
                    "1lTec5MjMsonC9wsRQDE19FujUP6/3fmPnY/09jbBySVJfJkpGspk/eOXuF1qcP8" +
                    "q+0g+OFO+fFrCsnuNFZTUrrtgZ6kkEaG00yKTFJvWDvAMiLxNIDlqfPzgTFQyNke" +
                    "WwFO58YiM4aZIg/3JClj+4r9FvHqGYu9iI3R3qGI01Z6jWdT2xHl8awPejdSr2zp" +
                    "yFAmD5BIwHO2NtLbzqHaPiaRegINqteYRxN0fHyOxaY0AQrsBJqCc1QEfSWimsjZ" +
                    "q0xqVsAG6l+qTqi9hK0UfVSBSSoAIHGMBH25ohqfMQKBgQDZgs4BE8F8zdgCdLq9" +
                    "5E2xOjx0khOYe89RWOgoRLi6HRGM2sF5LCoRZ5mV5HWZdbTsry9MzuJO+JFEF4SW" +
                    "Qq44kWdVvjpymqAZUS6Mk1s7KdLl/rbEx3IEgKyaSvHt7bskK2g0SZAzHfSVDje1" +
                    "cjMKRExJOf8TYv5ghdxj0hgCqQKBgQDN3LhLsjxXnRGr+agPyC8mUPSXffPzYb0m" +
                    "WZmLkSPEZTtZGRXCO+TAApjqFtoK75QDPKYeapMImB1FL8QpIot+fqhgB2ZfYUfh" +
                    "vnaEp25VrGlMvYVpvo+6SsCIBmwk/vy/vC+UdgBkT2S1Ma6ebFyDSNgYj7gjATbr" +
                    "6b/uMOM/+wKBgQCULdd3xmSvSHBbI/jAOtNu5HShGY7994LCMWZfYInUSM9W/QSH" +
                    "Lz4tgz2Pbwdql1S9VY9MrThhglc8YyUxVJXp9azHLgQVZR+if7rNi+nAAXsqsn8C" +
                    "ybj0NlDgACSW6bjvKB8iV+dD26lvDjOHihgZa3PpAvix1WPkj+0Xl95BmQKBgFCk" +
                    "m0mD6+GCdS7CYRS1ncrLwdUqkQsRibBhPyYOWE2SPnp7uitZnRWertpc01OfYIMY" +
                    "GhcmNq0SMb0tphFbJ/wIj9r2ayMID8md+/2hPTawkJtwTrgK33Dh4aQnp+uiO868" +
                    "YIMu6p5g/5l/eeEzn9YKDiUo/8AutJ4b9oTPf+ZLAoGBAJlqBpn0lOUWVSb/ja0R" +
                    "aTQMfXRH7tt4g1iH2dFP9VQOXwUMTk7doQPyAUhlNdadobOJwXppOUCO//YrXyhU" +
                    "xaucztjZTtkPLsFGZ2ryIAzYpupbOR8reluN8a3gLHjuw7NoUex1oWUQiLx/8uL8" +
                    "RpyxlQcWB+rKQcOtfK34B4p2";
    public final static String CHARSET = "utf-8";

    public final static String ALIPAY_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArulH9jqSRBHifsnS2VRK" +
                    "xbHftkaKoJT+ysA40hj2q7ku7Kv3vZZUeGicwL2HfoaHPVsQmrt6S1WFK6HxFRMg" +
                    "vGEAuL0/1PTCPyW0XxLPyhBUAJhfNKy8RTGQoWr0HM4bBL8kYbqN0dqWR1ZnA6Ki" +
                    "qVIZQw8a9WjxBvKYMXIj0WctjdzMHkIOZkVSYQpvo67+aaL5gQoEHpo+cUyloxfC" +
                    "jV0TihtsWl7OV6BrI+7FlUW64E5BsydLb9XAqNsgBiHhKyDzSZ5E1m4pYtbLs9eA" +
                    "Gepzks3gQgVR3RFAvh26xKIC905AHWkh8HSuOpXL2Zqt5xWgg2nNMq2DgwJ68g0y" +
                    "swIDAQAB";

    public final static String ALIPAY_CALLBACK_PUBLIC_KEY ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhS+NqT4gQhAyR/R+2CmvD8dQi5P05sJcNOKNDESDrHfSSGyToY3x65DQkp8Ilh8D5RoOAsnJw0OehesXfmtKnUgc22kyjPoUAKWoTTEQUsqqq120rHA5M7k1ThExHfQbRWr+jDTyLz+Q36tZzTY0hE5i9ncDQtNqec/ChlcYBybgOrKf2EGk5YSDf/N413TzJTWFSHJhllQVp2dfiliRfxAYENA3x+BAGtXhFyTMeQodUMecDer2DkfIbTbvvBF1U48Yy+4rxTXpmTWNyPPPzqtCZuJLSzV7EIUr8sPW9KJLWPInkZEEQfKuyaMvOsa6PtsqGSe7KZ3COYuVidDvuQIDAQAB";

    @Test
    public void aliPayTest1() throws Exception {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("我是测试数据");
        model.setSubject("App支付测试Java");
        model.setOutTradeNo("TEST44147747");
        model.setTimeoutExpress("30m");
        model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl("http://baidu.com");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void signTest(){
        String sign ="UgFR511FvbctZjiwehJP8EMGVFJqfg9tufZ4MtbqaCdXjw5jXIsulBLzTzJa5c7W/S+CHwW6df6BTqfb5lNBZ7l0Zzs2x2jsse+gUO7BNkOhdbQQrUvEI+lB0g32fETC6E+qYX2DXPSP0b6okQ6GaYDp9aMV1PYiJ9+v0/ka2XuYq7bTug18s33Gwk46JXdKOj9waHftnncdrYDZ9dtvBbQsz2F5fHLZVDPRSu/uFisUTdJxmeADiilJweUOWZzDDNhpKgBGO7G0C3YqT0o+FiKH/ohXdtIyDy9KLvw/HR/foVlJCT8++9hBu7t3WqwWKXXCd9S2YT0H3ADvefa2RA==";
        Map<String,String> map = new HashMap<>();
        map.put("sign",sign);
        try {
            boolean flag = AlipaySignature.rsaCheckV2(map, ALIPAY_CALLBACK_PUBLIC_KEY, "utf-8", "RSA2");
            System.out.println(flag);
        }catch (Exception e){
            e.printStackTrace();
        }



    }




}
