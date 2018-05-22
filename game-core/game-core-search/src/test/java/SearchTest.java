
import com.fulu.game.core.search.domain.ProductDoc;
import com.google.gson.GsonBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import org.junit.Test;


public class SearchTest {



    private static JestClient getJestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://10.0.3.105:9200")
                .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())
                .multiThreaded(true)
                .readTimeout(10000)
                .build());
        JestClient client = factory.getObject();
        return client;
    }


    @Test
    public void createIndex() throws Exception {
        JestClient jestClient = SearchTest.getJestClient();
        ProductDoc productDoc1 = new ProductDoc();
        productDoc1.setName("王者荣耀");
        productDoc1.setId(1);
        Index index1 = new Index.Builder(productDoc1).index("product-store").type("product").build();
        JestResult jestResult1 = jestClient.execute(index1);
        System.out.println(jestResult1.getJsonString());
    }


}
