import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.PlatformMoneyDetailsService;
import com.fulu.game.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class PlatformMoneyTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;


    @Test
    public void test1() {
        List<Order> orderList = orderService.findByStatusList(new Integer[]{300, 400, 401, 500, 501, 502, 600});
    }

    @Test
    public void test2(){
        SMSUtil.sendLeaveInform("15002781007","lalal","https://t-open.wzpeilian.com/pc/weixin.html ");
    }
}
