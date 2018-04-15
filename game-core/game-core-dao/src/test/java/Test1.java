import com.fulu.game.common.domain.Password;
import com.fulu.game.common.utils.EncryptUtil;
import com.fulu.game.core.CoreDaoApplication;
import com.fulu.game.core.dao.SysUserDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by bwang.abcft on 2018/4/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CoreDaoApplication.class)
@Slf4j
public class Test1 {

    @Autowired
    private SysUserDao sysUserDao;

    @Test
    public void  test1(){

        Password pass = EncryptUtil.PiecesEncode("123456");
        System.out.println(pass);

    }

}
