package com.fulu.generate.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by bwang.abcft on 2018/3/5.
 */
public class DBUtils {

    /**
     * 获取数据库连接
     * @param classDriver
     * @param uri
     * @param username
     * @param password
     * @return
     */
    public static Connection getConnectionByJDBC(String classDriver,String uri,String username,String password) {
        try {
            // 装载驱动包类
            Class.forName(classDriver);
            // 加载驱动
            return DriverManager.getConnection(uri, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println("装载驱动包出现异常!请查正！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("链接数据库发生异常!");
            e.printStackTrace();
        }
        return null;
    }

}
