

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
        codeGenerateForTable("wangbin","t_order_deal");
    }

    @Test
    public void test2() {
        codeGenerateForTable("yanbiao","t_banner");
    }

    @Test
    public void test3() {
        codeGenerateForTable("Gong Zechun","t_order");
    }

    public void codeGenerateForTable(String author,String ... tableName){
        String driver = "com.mysql.jdbc.Driver";
        String uri = "jdbc:mysql://10.0.3.105:3306/game_service?&characterEncoding=utf-8&useUnicode=true";
        String username = "root";
        String password = "123456";
        Connection connection = DBUtils.getConnectionByJDBC(driver, uri, username, password);
        GenerateFactory generateFactory = GenerateFactory.GenerateFactoryBuilder
                .aGenerateFactory()
                .withConnection(connection)
                .withAutoRemovePre(true)
                .withOutPath("E:\\generate_files")
                .withPackageName("com.fulu.game.core")
                .withAuthor(author)
                .build();
        generateFactory.generatorCode(tableName);
    }
}
