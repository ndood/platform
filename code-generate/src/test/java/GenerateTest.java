

import com.fulu.generate.GenerateFactory;
import com.fulu.generate.utils.DBUtils;
import org.junit.Test;

import java.sql.Connection;

/**
 * Created by bwang.abcft on 2018/3/5.
 */
public class GenerateTest {

    @Test
    public void test1() {
        String driver = "com.mysql.jdbc.Driver";
        String uri = "jdbc:mysql://10.11.255.60:3306/mapping?&characterEncoding=utf-8&useUnicode=true";
        String username = "mapping";
        String password = "cae8458046cb";

        Connection connection = DBUtils.getConnectionByJDBC(driver, uri, username, password);
        GenerateFactory generateFactory = GenerateFactory.GenerateFactoryBuilder
                .aGenerateFactory()
                .withConnection(connection)
                .withAutoRemovePre(true)
                .withOutPath("E:\\templates")
                .withPackageName("com.abcft.mapping")
                .withAuthor("wangbin")
                .build();

        generateFactory.generatorCode("mp_model_statis");

    }


    @Test
    public void test2() {
        String driver = "com.mysql.jdbc.Driver";
        String uri = "jdbc:mysql://10.11.255.60:3306/mapping?&characterEncoding=utf-8&useUnicode=true";
        String username = "mapping";
        String password = "cae8458046cb";

        Connection connection = DBUtils.getConnectionByJDBC(driver, uri, username, password);
        GenerateFactory generateFactory = GenerateFactory.GenerateFactoryBuilder
                .aGenerateFactory()
                .withConnection(connection)
                .withAutoRemovePre(true)
                .withOutPath("D:\\CodeSpace\\gencode")
                .withPackageName("com.abcft.mapping")
                .withAuthor("jiangqs")
                .build();

        generateFactory.generatorCode("mp_job_flow_instance");

    }



}
