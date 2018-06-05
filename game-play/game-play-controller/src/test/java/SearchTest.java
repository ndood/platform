import com.fulu.game.common.properties.Config;
import com.fulu.game.core.search.domain.ProductShowCaseDoc;
import com.fulu.game.play.PlayApplication;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class SearchTest {

    @Autowired
    private JestClient jestClient;
    @Autowired
    private Config configProperties;

    @Test
    public void test1()throws Exception{

    }

}
