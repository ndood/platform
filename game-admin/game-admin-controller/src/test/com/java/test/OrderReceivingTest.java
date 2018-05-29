package com.java.test;

import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.Data;
import lombok.Getter;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderReceivingTest {


    @Test
    public void test() {
        List<String> lines = FileUtil.readLines("E:\\data\\527.log", "utf-8");
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
        Pattern beginPattern = Pattern.compile("(.+) \\[http.*开始接单设置:userId:(\\d+);.+id=(\\d+),.+categoryId=(\\d+),.+;beginOrderDate:(\\d+);orderHour:(\\d\\.\\d);");
        Map<String,List<OrderStatistics>> startMap = new LinkedHashMap<>();
        for (String line : womanLines) {
            Matcher matcher = startOrderPattern.matcher(line);
            if(matcher.find()){
                Matcher beginMatcher = beginPattern.matcher(line);
                if(beginMatcher.find()){
                    OrderStatistics orderStatistics = new OrderStatistics();
                    orderStatistics.setTime(beginMatcher.group(1));
                    orderStatistics.setUserId(beginMatcher.group(2));
                    orderStatistics.setProductId(beginMatcher.group(3));
                    orderStatistics.setCategoryId(beginMatcher.group(4));
                    orderStatistics.setBeginOrderDate(beginMatcher.group(5));
                    orderStatistics.setOrderHour(beginMatcher.group(6));
                    String userCategoryKey = orderStatistics.getUserId()+"-"+orderStatistics.getCategoryId()+"-"+orderStatistics.getProductId();
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
        Pattern endPattern = Pattern.compile("(.+) \\[http.*停止接单设置:userId:(\\d+);.+id=(\\d+),.+categoryId=(\\d+),.+;endOrderDate:(\\d+);");
        Map<String,List<OrderStatistics>> endMap = new LinkedHashMap<>();
        for (String line : womanLines) {
            Matcher matcher = endOrderPattern.matcher(line);
            if(matcher.find()){
                Matcher endMatcher = endPattern.matcher(line);
                if(endMatcher.find()){
                    OrderStatistics orderStatistics = new OrderStatistics();
                    orderStatistics.setTime(endMatcher.group(1));
                    orderStatistics.setUserId(endMatcher.group(2));
                    orderStatistics.setProductId(endMatcher.group(3));
                    orderStatistics.setCategoryId(endMatcher.group(4));
                    orderStatistics.setEndOrderDate(endMatcher.group(5));
                    String userCategoryKey = orderStatistics.getUserId()+"-"+orderStatistics.getCategoryId()+"-"+orderStatistics.getProductId();
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

        List<String> rightStr = new ArrayList<>();
        startMap.forEach((sk,sv)->{
            //如果存在结束接单的操作
            if(endMap.containsKey(sk)){
                final BigDecimal[] total = {new BigDecimal(0)};
                sv.forEach((sp)->{
                   Date startTime = DateUtil.parse(sp.getTime());
                   Iterator<OrderStatistics> it =endMap.get(sk).iterator();
                  boolean flag = true;
                  while (it.hasNext()){
                       OrderStatistics ep = it.next();
                       Date endTime = DateUtil.parse(ep.getTime());
                       if(endTime.before(startTime)){
                           continue;
                       }else{
                           if(endTime.before(DateUtil.offsetHour(startTime,new BigDecimal(sp.getOrderHour()).intValue()))){
                               BigDecimal timeSub = new BigDecimal(ep.getEndOrderDate()).subtract(new BigDecimal(sp.getBeginOrderDate()));
                               java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.00");
                               BigDecimal result = timeSub.divide(new BigDecimal(1000 * 60 * 60),1, BigDecimal.ROUND_HALF_EVEN);
                               it.remove();
                               flag =false;
                               total[0] = total[0].add(result);
                               break;
                           }
                       }
                   }

                   if(flag){
                        total[0] = total[0].add(new BigDecimal(sp.getOrderHour()));
                   }
                });
                if(total[0].compareTo(new BigDecimal(6))>=0){
                    rightStr.add(sk);
                    System.out.println(sk);
                }
            }else{
                final BigDecimal[] total = {new BigDecimal(0)};
                sv.forEach((sp)->{
                    total[0] = total[0].add(new BigDecimal(sp.getOrderHour()));
                });
                if(total[0].compareTo(new BigDecimal(6))>=0){
                    rightStr.add(sk);
                    System.out.println(sk);
                }
            }
        });

        System.out.println(rightStr);

        //按照分类去重
        Set<String> sets = new HashSet<>();
        for(String str : rightStr){
            String[] userArr = str.split("-");
            String userId = userArr[0];
            String categoryId = userArr[1];
            String productId = userArr[2];
            sets.add(userId+"-"+categoryId);
        }
        //计算去重后的结果
        Map<String,Integer> result = new HashMap<>();
        sets.forEach((k)->{
            String[] userArr = k.split("-");
            String userId = userArr[0];
            String categoryId = userArr[1];
            if(result.containsKey(userId)){
                result.put(userId,result.get(userId)+1);
            }else{
                result.put(userId,1);
            }
        });

        StringBuffer stringBuffer = new StringBuffer();
        result.forEach((k,v)->{
            if(v>1){
                stringBuffer.append(k).append(",");
            }
        });
        System.out.println(stringBuffer.toString());



        FileUtil.writeLines(errLog,"E:\\data\\525-err.log","utf-8",false);

    }

    @Data
    class OrderStatistics{
        private String userId;
        private String categoryId;
        private String productId;
        private String beginOrderDate;
        private String orderHour;
        private String endOrderDate;
        private String time;
    }




}
