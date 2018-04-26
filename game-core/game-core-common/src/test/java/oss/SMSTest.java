package oss;

import com.fulu.game.common.utils.SMSUtil;

import java.util.HashMap;
import java.util.Set;

public class SMSTest {



    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        HashMap<String, Object> result = null;

        SMSUtil restAPI = new SMSUtil();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da85f341b69015f4dbe74580a0c", "6f9dc07aa412497a995451f9463b1f61");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da85f341b69015f4dbe75f30a13");// 初始化应用ID
        result = restAPI.sendTemplateSMS("18801285391","216457" ,new String[]{"hello test"});

        System.out.println("SDKTestSendTemplateSMS result=" + result);

        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }


}
