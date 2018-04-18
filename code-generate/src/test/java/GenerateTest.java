

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
        String uri = "jdbc:mysql://localhost:3306/accompany_play_db?&characterEncoding=utf-8&useUnicode=true";
        String username = "root";
        String password = "";

        Connection connection = DBUtils.getConnectionByJDBC(driver, uri, username, password);
        GenerateFactory generateFactory = GenerateFactory.GenerateFactoryBuilder
                .aGenerateFactory()
                .withConnection(connection)
                .withAutoRemovePre(true)
                .withOutPath("E:\\templates")
                .withPackageName("com.fulu.game.core")
                .withAuthor("wangbin")
                .build();

        generateFactory.generatorCode("t_tag","t_tech_attr","t_tech_value");
    }





}
