import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;

import java.net.URL;
import java.util.BitSet;
import java.util.Date;

public class Test {


    public static void main(String[] args) {

        int [] array = new int [] {1,2,3,22,0,3,7,8};
        BitSet bitSet  = new BitSet(6);
        //将数组内容组bitmap
        for(int i=0;i<array.length;i++)
        {
            bitSet.set(array[i], true);
        }
        System.out.println(bitSet.size());
        System.out.println(bitSet.get(7));
        System.out.println(bitSet.toLongArray());
    }



}
