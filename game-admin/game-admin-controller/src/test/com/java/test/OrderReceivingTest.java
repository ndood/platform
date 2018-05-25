package com.java.test;

import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileUtil;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderReceivingTest {


    @Test
    public void test() {
        List<String> lines = FileUtil.readLines("E:\\data\\0523.log", "utf-8");
        // 创建 Pattern 对象
        Pattern genderPattern = Pattern.compile("gender=2");

        List<String> womanLines = new ArrayList<>();
        for (String line : lines) {
            if(genderPattern.matcher(line).find()){
                womanLines.add(line);
            }
        }

        //查找所有接单用户的ID
        Pattern startOrderPattern = Pattern.compile("开始接单设置:userId:(\\d+);");
        Set<Integer> userIds = new LinkedHashSet<>();
        for (String line : womanLines) {
            Matcher matcher = startOrderPattern.matcher(line);
            if(matcher.find()){
                userIds.add(Integer.valueOf(matcher.group(1)));
            }
        }

        //按用户分类所有的日志
        Map<Integer,List<String>> userMap = new LinkedHashMap<>();
        for(Integer userId: userIds){
            Pattern userOrderPattern = Pattern.compile("接单设置:userId:"+userId+";");
            for (String line : womanLines) {
                Matcher matcher = userOrderPattern.matcher(line);
                if(matcher.find()){
                    if(userMap.containsKey(userId)){
                        userMap.get(userId).add(line);
                    }else{
                        userMap.put(userId, Lists.newArrayList(line));
                    }
                }
            }
        }

        //查询满足两个用户商品的用户
        Set<Integer> rightUser = new LinkedHashSet<>();
        userMap.forEach((k,v)->{
            v.forEach((line)->{
                int size = v.size();
                if(size>=4){
                    rightUser.add(k);
                }
            });
        });

        //排除停止接单的用户
        userMap.forEach((k,v)->{
            if(rightUser.contains(k)){
                Pattern userOrderPattern = Pattern.compile("停止接单设置");
                final int[] size = {v.size()};
                v.forEach((line)->{
                    if(userOrderPattern.matcher(line).find()){
                        size[0]--;
                    }
                });
                if(size[0]<4){
                    rightUser.remove(k);
                }
            }
        });


        userMap.forEach((k,v)->{
            if(rightUser.contains(k)){
                FileUtil.writeLines(v,"E:\\data\\0523_result.log","utf-8",true);
            }
        });


    }

}
