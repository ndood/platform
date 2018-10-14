package com.fulu.generate.utils;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.text.WordUtils;

import java.io.*;

/**
 * 代码生成器   工具类
 */
public class GenUtils {

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }


    public static String getJavaType(String dataType){
        Configuration config = getConfig();
        String attrType = config.getString(dataType, "unknowType");
        return attrType;
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName,Boolean autoRemovePre) {
        if (autoRemovePre) {
            tableName = tableName.substring(tableName.indexOf("_") + 1);
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new RuntimeException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className) {
        if(template.contains("Entity.java.vm")){
            template = template.replace("Entity","");
        }
        if(template.contains("EntityBO.java.vm")){
            template = template.replace("Entity","");
        }
       return className+template.replace(".vm","");
    }


    /**
     * 写入文件
     * @Title: writeFile
     * @param filePathAndName
     * @param fileContent
     */
    public static void writeFile(String filePathAndName, String fileContent) {
        try {
            File f = new File(filePathAndName);
            if(!f.getParentFile().exists()){
                if(!f.getParentFile().mkdirs()) {
                    System.out.println("创建目标文件所在目录失败！");
                    return;
                }
            }
            if (!f.exists()) {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(fileContent);
            writer.close();
            System.out.println(filePathAndName+"模型生成成功!");
        } catch (Exception e) {
            System.out.println("写文件内容操作出错");
            e.printStackTrace();
        }
    }


    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                System.out.println("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }
}
