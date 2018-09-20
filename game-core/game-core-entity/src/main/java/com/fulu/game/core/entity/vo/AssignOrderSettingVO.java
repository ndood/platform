package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.AssignOrderSetting;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 派单设置
 *
 * @author wangbin
 * @date 2018-09-20 18:43:50
 */
@Data
public class AssignOrderSettingVO  extends AssignOrderSetting {


    private Integer[] weekDays;



    public void setWeekDays(Integer[] weekDays) {
        this.weekDays = weekDays;
        if(weekDays!=null&&weekDays.length>0){
            setWeekDayBins(intArr2Bin(weekDays));
        }
    }


    public Integer[] getWeekDays() {
        Integer bin = getWeekDayBins();
        if(bin==null){
            return null;
        }
        return bin2Location(bin);
    }



    public Integer[] bin2Location(int bin){
        List<Integer> list = new ArrayList();
        String c = Integer.toBinaryString(bin);
        char[] s = c.toCharArray();
        for(int i =0;i<s.length;i++){
            if(String.valueOf(s[i]).equals("1")){
                list.add(i+1);
            }
        }
        return list.toArray(new Integer[]{});
    }


    public int intArr2Bin(Integer[] locationArr) {
        String[] binArr = new String[]{"0", "0", "0", "0", "0", "0", "0"};
        for (int i : locationArr) {
            binArr[i - 1] = "1";
        }
        return Integer.parseInt(StringUtils.join(binArr), 2);
    }

}
