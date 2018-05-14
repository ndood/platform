import com.fulu.game.common.cache.SysConfigGuavaCache;
import com.fulu.game.core.entity.SysConfig;
import com.fulu.game.core.service.SysConfigService;
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
public class CacheTest {

    @Autowired
    private SysConfigService sysConfigService;

    @Test
    public void  test1(){
        List<SysConfig> sysConfigList = sysConfigService.findAll();
        System.out.println(sysConfigList);
    }
}
