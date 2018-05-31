import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.play.schedule.PlayScheduleApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayScheduleApplication.class)
@Slf4j
public class OssTest {

    @Autowired
    private OssUtil ossUtil;

    @Test
    public void test1(){
        String file ="http://test-game-play.oss-cn-hangzhou.aliyuncs.com/2018/5/8/f418afe6cffc486f85779750fd0965e4.jpg";
        try {
            URL url = new URL(file);
            String key = url.getPath().substring(1);
            ossUtil.deleteFile(key);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
