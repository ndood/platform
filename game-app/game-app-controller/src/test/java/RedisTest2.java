import com.fulu.game.app.PlayApplication;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class RedisTest2 {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisOpenServiceImpl redisOpenServiceImpl;


    @Test
    public void test1() {
        User user1 = new User();
        user1.setId(1);
        user1.setNickname("zhangsan1");

        User user2 = new User();
        user2.setId(2);
        user2.setNickname("zhangsan2");

        redisTemplate.opsForList().leftPushAll(RedisKeyEnum.USER_ONLINE_KEY.generateKey("11111"),0,1,2,3,4,5,6,7,8);

        redisTemplate.opsForList().set(RedisKeyEnum.USER_ONLINE_KEY.generateKey("11111"),0,user1);
        redisTemplate.opsForList().set(RedisKeyEnum.USER_ONLINE_KEY.generateKey("11111"),5,user2);
    }

    @Test
    public void test2() {
        long size = redisTemplate.opsForList().size(RedisKeyEnum.USER_ONLINE_KEY.generateKey("11111"));
        System.out.println(size);
    }

    @Test
    public void test3() {
        User user = new User();
        user.setId(1);
        user.setNickname("zhangsan1");
        Object o =    redisTemplate.opsForList().index(RedisKeyEnum.USER_ONLINE_KEY.generateKey("11111"),5);
        System.out.println(o);
    }

    @Test
    public void test4() {
        List<Object> set = redisTemplate.opsForList().range(RedisKeyEnum.USER_ONLINE_KEY.generateKey("11111"),0,-1);
        System.out.println(set);
    }


    @Test
    public void test5() {

        User user1 = new User();
        user1.setId(1);
        user1.setNickname("zhangsan1");
        User user2 = new User();
        user2.setId(2);
        user2.setNickname("zhangsan2");
        redisTemplate.opsForZSet().add(RedisKeyEnum.USER_ONLINE_KEY.generateKey("2222"),user1,1);
        redisTemplate.opsForZSet().add(RedisKeyEnum.USER_ONLINE_KEY.generateKey("2222"),user2,5);
    }



    @Test
    public void test6() {
        Set set1 = redisTemplate.opsForZSet().range(RedisKeyEnum.USER_ONLINE_KEY.generateKey("2222"),0,-1);
        Set set2 =redisTemplate.opsForZSet().range(RedisKeyEnum.USER_ONLINE_KEY.generateKey("2222"),4,5);
    }



}
