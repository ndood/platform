import com.fulu.game.core.service.PlatformMoneyDetailsService;
import com.fulu.game.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class PlatformMoneyTest {

    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;

    @Test
    public void test1(){

        for(int i =0;i<100;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    platformMoneyDetailsService.createOrderDetails("123456",new BigDecimal(1));
                }
            }).start();
        }

    }

}
