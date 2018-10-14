package com.fulu.generate;


import com.fulu.generate.entity.Column;
import com.fulu.generate.entity.Table;
import com.fulu.generate.utils.GenUtils;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by bwang.abcft on 2018/3/5.
 */
public class GenerateFactory {

    /**
     * 数据库名称
     */
    private Connection connection;
    /**
     * 是否去前缀
     */
    private Boolean autoRemovePre;

    /**
     * 生成包名
     */
    private String packageName;
    /**
     * 输出目录
     */
    private String outPath;
    /**
     * 作者名称
     */
    private String author;

    private static final String GENERATOR_PATH = "generator/";

    private static final String[] TEMPLATES = new String[]{"Mapper.xml.vm","Entity.java.vm","Dao.java.vm","Service.java.vm","ServiceImpl.java.vm","EntityBO.java.vm"};


    /**
     * 获取数据库表信息
     *
     * @param tableName
     * @return
     */
    public Map<String, String> getTableInfo(String tableName) {
        String sql = "select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables "
                + "	where table_schema = (select database()) and table_name = '" + tableName + "'";
        Map<String, String> result = new HashMap<>();
        try (Statement stmt = this.connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result.put("tableName", rs.getString("tableName"));
                result.put("tableComment", rs.getString("tableComment"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取表字段信息
     *
     * @param tableName
     * @return
     */
    public List<Map<String, String>> getColumnInfo(String tableName) {
        String sql = "select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns "
                + " where table_name = '" + tableName + "' and table_schema = (select database()) order by ordinal_position";
        List<Map<String, String>> resultList = new ArrayList<>();
        try (Statement stmt = this.connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Map<String, String> item = new HashMap<>();
                item.put("columnName", rs.getString("columnName"));
                item.put("dataType", rs.getString("dataType"));
                item.put("columnComment", rs.getString("columnComment"));
                item.put("columnKey", rs.getString("columnKey"));
                resultList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }


    public void generatorCode(String... tableNames) {
        for (String tableName : tableNames) {
            Map<String, String> table = getTableInfo(tableName);
            if(table.isEmpty()){
                throw new RuntimeException("数据库不存在表名:"+tableName);
            }
            List<Map<String, String>> columns = getColumnInfo(tableName);
            generatorCode(table,columns);
        }
    }


    private void generatorCode(Map<String, String> table, List<Map<String, String>> columns) {
        //表信息
        Table tableDO = new Table();
        tableDO.setTableName(table.get("tableName"));
        tableDO.setComments(table.get("tableComment"));
        //表名转换成Java类名
        String className = GenUtils.tableToJava(tableDO.getTableName(), this.autoRemovePre);
        tableDO.setClassName(className);
        tableDO.setClassname(StringUtils.uncapitalize(className));

        //列信息
        List<Column> columnList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            Column columnDO = new Column();
            columnDO.setColumnName(column.get("columnName"));
            columnDO.setDataType(column.get("dataType"));
            columnDO.setComments(column.get("columnComment"));
            columnDO.setExtra(column.get("extra"));
            //列名转换成Java属性名
            String attrName = GenUtils.columnToJava(columnDO.getColumnName());
            columnDO.setAttrName(attrName);
            columnDO.setAttrname(StringUtils.uncapitalize(attrName));
            //列的数据类型，转换成Java类型
            String attrType = GenUtils.getJavaType(columnDO.getDataType());
            columnDO.setAttrType(attrType);

            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableDO.getPk() == null) {
                tableDO.setPk(columnDO);
            }

            columnList.add(columnDO);
        }
        tableDO.setColumns(columnList);

        //没主键，则第一个字段为主键
        if (tableDO.getPk() == null) {
            tableDO.setPk(tableDO.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableDO.getTableName());
        map.put("comments", tableDO.getComments());
        map.put("pk", tableDO.getPk());
        map.put("className", tableDO.getClassName());
        map.put("classname", tableDO.getClassname());
        map.put("pathName", this.packageName.substring(this.packageName.lastIndexOf(".") + 1));
        map.put("columns", tableDO.getColumns());
        map.put("package", this.packageName);
        map.put("author", this.author);
        map.put("datetime", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        for (String template : TEMPLATES) {
            String fileName = GenUtils.getFileName(template, tableDO.getClassName());
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(GENERATOR_PATH+template, "UTF-8");
            tpl.merge(context, sw);
            String modelName = this.outPath + File.separator + tableDO.getClassName()+File.separator+fileName;
            /**
             * 文件写入流
             */
            GenUtils.writeFile(modelName, sw.toString());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Boolean getAutoRemovePre() {
        return autoRemovePre;
    }

    public void setAutoRemovePre(Boolean autoRemovePre) {
        this.autoRemovePre = autoRemovePre;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public static final class GenerateFactoryBuilder {
        private Connection connection;
        private Boolean autoRemovePre;
        private String packageName;
        private String outPath;
        private String author;

        private GenerateFactoryBuilder() {
        }

        public static GenerateFactoryBuilder aGenerateFactory() {
            return new GenerateFactoryBuilder();
        }

        public GenerateFactoryBuilder withConnection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public GenerateFactoryBuilder withAutoRemovePre(Boolean autoRemovePre) {
            this.autoRemovePre = autoRemovePre;
            return this;
        }

        public GenerateFactoryBuilder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public GenerateFactoryBuilder withOutPath(String outPath) {
            this.outPath = outPath;
            return this;
        }

        public GenerateFactoryBuilder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public GenerateFactory build() {
            GenerateFactory generateFactory = new GenerateFactory();
            generateFactory.setConnection(connection);
            generateFactory.setAutoRemovePre(autoRemovePre);
            generateFactory.setPackageName(packageName);
            generateFactory.setOutPath(outPath);
            generateFactory.setAuthor(author);
            return generateFactory;
        }
    }
}
