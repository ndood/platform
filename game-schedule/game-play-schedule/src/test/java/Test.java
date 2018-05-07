import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;

import java.util.Date;

public class Test {


    public static void main(String[] args) {
        long hour = DateUtil.between(new Date(),new Date(), DateUnit.HOUR);
        System.out.println(hour);
    }



}
