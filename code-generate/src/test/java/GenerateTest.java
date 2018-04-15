

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
        String uri = "jdbc:mysql://localhost:3306/play_admin_db?&characterEncoding=utf-8&useUnicode=true";
        String username = "root";
        String password = "123456";

        Connection connection = DBUtils.getConnectionByJDBC(driver, uri, username, password);
        GenerateFactory generateFactory = GenerateFactory.GenerateFactoryBuilder
                .aGenerateFactory()
                .withConnection(connection)
                .withAutoRemovePre(false)
                .withOutPath("E:\\templates")
                .withPackageName("com.fulu.game.core")
                .withAuthor("wangbin")
                .build();

        generateFactory.generatorCode("sys_user","sys_role","sys_permission","sys_role_permission","sys_user_role");
    }





}
