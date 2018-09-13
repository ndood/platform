import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.junit.Test;

import java.util.Date;

public class Test1 {



    @Test
    public void test1(){
          Date date1 =  DateUtil.parse("2018-09-13 14:00");
          Date date2 =  new Date();

          Long min =DateUtil.between(date2,date1, DateUnit.MINUTE);
        System.out.println(min);
    }
}
