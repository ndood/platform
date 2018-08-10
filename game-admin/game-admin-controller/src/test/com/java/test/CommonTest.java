package com.java.test;


import cn.hutool.core.util.StrUtil;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonTest {

    @Test
    public void test1(){
        String orderBy = "asd_ad_fsd desc";
        String result = StrUtil.toUnderlineCase(orderBy);
        System.out.println(result);
    }



    @Test
    public void test2() throws Exception {
        Integer a = 127;
        int b = 127;
        System.out.println(a==b);

    }


}
