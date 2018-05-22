import com.fulu.game.common.properties.Config;
import com.fulu.game.core.search.domain.ProductDoc;
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
        ProductDoc product1 = new ProductDoc();
        product1.setId(1);
        product1.setName("王者荣耀");

        ProductDoc product2 = new ProductDoc();
        product2.setId(2);
        product2.setName("英雄联盟");

        Index index1 = new Index.Builder(product1)
                .index(configProperties.getElasticsearch().getIndexDB())
                .type("product")
                .build();

        Index index2 = new Index.Builder(product2)
                .index(configProperties.getElasticsearch().getIndexDB())
                .type("product")
                .build();


        jestClient.execute(index1);
        jestClient.execute(index2);
    }

}
