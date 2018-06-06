import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisOpenServiceImpl redisOpenServiceImpl;

    /**
     * 测试key-String
     */
    @Test
    public void testValue() {
        //default JdkSerializationRedisSerializer
        //StringRedisSerializer
        //Jackson2JsonRedisSerializer

        redisOpenServiceImpl.set("xxada","123",10);
    }

    /**
     * 测试key-hash
     */
    @Test
    public void testHash() {

        redisTemplate.opsForValue().setBit("user2.flags",0,true);

        redisTemplate.opsForValue().getBit("user2.flags",0);

        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);

        HashOperations<String, String, Object> opsForHash = redisTemplate.opsForHash();
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        opsForHash.putAll("hh", map);
    }

    @Test
    public void testBitSetIn(){
        redisTemplate.opsForValue().setBit("user2.flags",0,true);
        redisTemplate.opsForValue().setBit("user2.flags",2,true);
        redisTemplate.opsForValue().setBit("user2.flags",3,true);
        redisTemplate.opsForValue().setBit("user2.flags",4,true);
        redisTemplate.opsForValue().setBit("user2.flags",5,true);
        redisTemplate.opsForValue().setBit("user2.flags",6,true);
        redisTemplate.opsForValue().setBit("user2.flags",7,true);
        redisTemplate.opsForValue().setBit("user2.flags",8,true);

    }

   @Test
    public void testBitSetOut(){
       System.out.println(redisTemplate.opsForValue().getBit("user2.flags",5));
       System.out.println( redisTemplate.opsForValue().getBit("user2.flags",6));
       System.out.println(redisTemplate.opsForValue().getBit("user2.flags",7));
       System.out.println( redisTemplate.opsForValue().getBit("user2.flags",8));
       System.out.println(  redisTemplate.opsForValue().getBit("user2.flags",9));
       System.out.println(  redisTemplate.opsForValue().getBit("user2.flags",10));
       System.out.println(  redisTemplate.opsForValue().getBit("user2.flags",11));
       System.out.println(  redisTemplate.opsForValue().getBit("user2.flags",12));

    }



}
