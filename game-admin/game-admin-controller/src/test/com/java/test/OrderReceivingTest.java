package com.java.test;

import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.Data;
import lombok.Getter;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderReceivingTest {


    @Test
    public void test() {
        List<String> lines = FileUtil.readLines("E:\\data\\524.log", "utf-8");
        // 创建 Pattern 对象
        Pattern genderPattern = Pattern.compile("gender=2");

        List<String> womanLines = new ArrayList<>();
        for (String line : lines) {
            if(genderPattern.matcher(line).find()){
                womanLines.add(line);
            }
        }

        //查找所有开始接单的记录
        List<String> errLog = new ArrayList<>();
        Pattern startOrderPattern = Pattern.compile("开始接单设置:userId:(\\d+).+categoryId=(\\d+),");
        Pattern beginPattern = Pattern.compile("开始接单设置:userId:(\\d+);\\(id=(\\d+),.+categoryId=(\\d+),.+;beginOrderDate:(\\d+);orderHour:(\\d\\.\\d);");
        Map<String,List<OrderStatistics>> startMap = new LinkedHashMap<>();
        for (String line : womanLines) {
            Matcher matcher = startOrderPattern.matcher(line);
            if(matcher.find()){
                Matcher beginMatcher = beginPattern.matcher(line);
                if(beginMatcher.find()){
                    String userCategoryKey = beginMatcher.group(1)+"-"+beginMatcher.group(2);
                    OrderStatistics orderStatistics = new OrderStatistics();
                    orderStatistics.setUserId(beginMatcher.group(1));
                    orderStatistics.setCategoryId(beginMatcher.group(2));
                    orderStatistics.setBeginOrderDate(beginMatcher.group(3));
                    orderStatistics.setOrderHour(beginMatcher.group(4));
                    if(startMap.containsKey(userCategoryKey)){
                        startMap.get(userCategoryKey).add(orderStatistics);
                    }else{
                        startMap.put(userCategoryKey,Lists.newArrayList(orderStatistics));
                    }
                }else{
                    errLog.add(line);
                }
            }
        }
        //查找所有结束接单的记录
        Pattern endOrderPattern = Pattern.compile("停止接单设置:userId:(\\d+).+categoryId=(\\d+),");
        Pattern endPattern = Pattern.compile("停止接单设置:userId:(\\d+);\\(id=(\\d+),.+categoryId=(\\d+),.+;endOrderDate:(\\d+);");
        Map<String,List<OrderStatistics>> endMap = new LinkedHashMap<>();
        for (String line : womanLines) {
            Matcher matcher = endOrderPattern.matcher(line);
            if(matcher.find()){
                Matcher endMatcher = endPattern.matcher(line);
                if(endMatcher.find()){
                    String userCategoryKey = endMatcher.group(1)+"-"+endMatcher.group(2);
                    OrderStatistics orderStatistics = new OrderStatistics();
                    orderStatistics.setUserId(endMatcher.group(1));
                    orderStatistics.setCategoryId(endMatcher.group(2));
                    orderStatistics.setEndOrderDate(endMatcher.group(3));
                    if(endMap.containsKey(userCategoryKey)){
                        endMap.get(userCategoryKey).add(orderStatistics);
                    }else{
                        endMap.put(userCategoryKey,Lists.newArrayList(orderStatistics));
                    }
                }else{
                    errLog.add(line);
                }
            }
        }


        System.out.println(startMap);
        System.out.println(endMap);
        FileUtil.writeLines(errLog,"E:\\data\\524-err.log","utf-8",false);

    }

    @Data
    class OrderStatistics{
        private String userId;
        private String categoryId;
        private String productId;
        private String beginOrderDate;
        private String orderHour;
        private String endOrderDate;
    }



}
