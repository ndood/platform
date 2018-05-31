import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;

import java.net.URL;
import java.util.Date;

public class Test {


    public static void main(String[] args) {

        String url ="http://test-game-play.oss-cn-hangzhou.aliyuncs.com/2018/5/8/f418afe6cffc486f85779750fd0965e4.jpg";

        try {
            URL url1 = new URL(url);
            System.out.println(url1.getPath());
        }catch (Exception e){

        }

    }



}
