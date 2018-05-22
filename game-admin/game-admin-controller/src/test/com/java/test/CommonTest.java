package com.java.test;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonTest {

    @Test
    public void test1(){
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
//比如timestamp=1449210225945；
            long date_temp = Long.valueOf(1526959589309L);
            String date_string = sdf.format(new Date(date_temp));
            System.out.println(date_string);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
