import com.fulu.game.core.CoreDaoApplication;
import com.fulu.game.core.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * Created by burgl on 2018/4/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CoreDaoApplication.class)
@Slf4j
public class RedisTest {


    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testObj() throws Exception {
        User user=new User();
        user.setNickname("WANGBIN");
        user.setRealname("王彬");
        ValueOperations<String, User> operations=redisTemplate.opsForValue();
        operations.set("com.neox", user);
        operations.set("com.neo.f", user,1, TimeUnit.SECONDS);

        Thread.sleep(1000);
        //redisTemplate.delete("com.neo.f");
        boolean exists=redisTemplate.hasKey("com.neo.f");

        Object user1 =    redisTemplate.opsForValue().get("com.neox");
        System.out.println(user1);

        if(exists){
            System.out.println("exists is true");
        }else{
            System.out.println("exists is false");
        }
        // Assert.assertEquals("aa", operations.get("com.neo.f").getUserName());
    }
}
