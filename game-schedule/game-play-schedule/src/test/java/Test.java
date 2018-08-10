import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class Test {


    public static void main(String[] args) {


       Date date = DateUtil.offset(new Date(),DateField.HOUR,(-24*7)+1);
        System.out.println(date);
    }



}
