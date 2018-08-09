package oss;

import com.fulu.game.common.enums.WechatTemplateMsgEnum;

public class OssTest {



    public static void main(String[] args) {

        WechatTemplateMsgEnum wechatTemplateMsgEnum =  WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY.choice(2);
        WechatTemplateMsgEnum wechatTemplateMsgEnum1 =  WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY;
        WechatTemplateMsgEnum wechatTemplateMsgEnum2 =  WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY.choice(1);

        System.out.println(wechatTemplateMsgEnum);
        System.out.println(wechatTemplateMsgEnum1);
        System.out.println(wechatTemplateMsgEnum2);

    }




}
