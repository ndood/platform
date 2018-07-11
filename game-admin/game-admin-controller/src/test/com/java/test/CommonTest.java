package com.java.test;

import com.xiaoleilu.hutool.util.StrUtil;
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

}
