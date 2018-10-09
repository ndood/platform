package com.fulu.game.common.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component(value = "configProperties")
@Accessors(chain = true)
@ConfigurationProperties(prefix = "config")
@Data
@Slf4j
@Validated
public class Config {

    private Evn evn = new Evn();

    private Oss oss = new Oss();

    private Cloopen cloopen = new Cloopen();

    private Wechat wechat_game = new Wechat();

    private Wechat wechat_poit = new Wechat();
    //微信公众号
    private Wechat wechat_mp = new Wechat();

    private Wechat wechat_app = new Wechat();

    private AppleStore appleStore = new AppleStore();

    private Elasticsearch elasticsearch = new Elasticsearch();

    //环信账户配置信息
    private Im im = new Im();

    private Jpush jpush = new Jpush();

    private Queue queue = new Queue();

    private Ordermail ordermail = new Ordermail();

    private AlipayPay alipayPay = new AlipayPay();

    private Fenqile fenqile = new Fenqile();

    @Data
    public static class Evn {
        private String prefix;
    }

    @Data
    public static class Oss {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String host;
    }

    @Data
    public static class Cloopen {
        private String serverIp;
        private String serverPort;
        private String accountSid;
        private String accountToken;
        private String appId;
    }


    @Data
    public static class Wechat {
        private String appId;
        private String secret;
        private String token;
        private String aesKey;
        private String msgDataFormat;
        private String mchId;
        private String mchKey;
        private String subAppId;
        private String subMchId;
        private String keyPath;
        private String tradeType;
        //订单支付回调
        private String notifyUrl;
        //充值支付回调
        private String payVirtualProductNotifyUrl;

    }

    @Data
    public static class Im {
        private String urlPrefix;
        private String appKey;
        private String orgName;
        private String appName;
        private String grantType;
        private String clientId;
        private String clientSecret;
    }

    @Data
    public static class Elasticsearch{
        private String host;
        private int readTimeout;
        private String indexDB;
        private String username;
        private String password;
    }

    @Data
    public static class Jpush{
        private String appKey;
        private String appSecret;
    }

    @Data
    public static class Ordermail {
        private String address;
        private String password;
        private String targetAddress;
    }

    @Data
    public static class Queue{
        private boolean miniappPush;
        private boolean appPush;
        private boolean userScore;
        private boolean formId;
    }

    @Data
    public static class AlipayPay{
        private String appId;
        private String appPrivateKey;
        private String alipayPublicKey;
        private String alipayCallBackPublicKey;
        private String payOrderNotifyUrl;
        private String payVirtualProductNotifyUrl;
        private String payGateway;
    }

    @Data
    public static class AppleStore{
        private String checkPayUrl;
    }


    @Data
    public static class Fenqile{
        private String sellerId;
        private String clientId;
        private String clientSecret;
        private String version;
        private String partnerId;
        private String partnerKey;
        private String orderNoticeUrl;
        private String orderDetailsUrl;
    }
}
