import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.core.entity.vo.AppPushExtras;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PushTest {


    @Test
    public void  test1(){

        Map<String,Object> param = new HashMap<>();
        param.put("id",1);
        AppPushExtras appPushExtras = new AppPushExtras();
        appPushExtras.setAndroidRoute("/xxx/index");
        appPushExtras.setIosRoute("/xxx/index");
        appPushExtras.setRouteType(1);
        appPushExtras.setBusinessType(1);
        appPushExtras.setParam(param);
        String result1 = JSONObject.toJSONString(appPushExtras);
        System.out.println(result1);


        AppPushExtras appPushExtras1 = new AppPushExtras();
        appPushExtras1.setAndroidRoute("http://abc.abc.com/xxx.html");
        appPushExtras1.setIosRoute("http://abc.abc.com/xxx.html");
        appPushExtras1.setRouteType(2);
        appPushExtras1.setBusinessType(1);
        appPushExtras1.setParam(param);
        String result2 = JSONObject.toJSONString(appPushExtras1);
        System.out.println(result2);


    }
}
