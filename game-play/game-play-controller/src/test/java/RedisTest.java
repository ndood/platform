import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class RedisTest {
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void  test1(){
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Map<String, String> map = new HashMap<String, String>();
        map.put("map1", "fiala1");
        map.put("map2", "fiala2");
        hashOperations.putAll("hash", map);

    }
}
