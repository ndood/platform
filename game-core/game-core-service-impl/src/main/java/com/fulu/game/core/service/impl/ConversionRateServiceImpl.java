package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.utils.DateUtils;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.ConversionRateVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ConversionRateDao;
import com.fulu.game.core.entity.ConversionRate;
import com.fulu.game.core.service.ConversionRateService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ConversionRateServiceImpl extends AbsCommonService<ConversionRate,Integer> implements ConversionRateService {

    @Autowired
	private ConversionRateDao conversionRateDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    @Override
    public ICommonDao<ConversionRate, Integer> getDao() {
        return conversionRateDao;
    }

    /**
     * 统计转化率
     */
    @Override
    public void statisticsConversionRate() {
        List<ConversionRateVO> searchList = getDateTimeList();
        //插入历史数据
        conversionRateDao.selectInto();
        //删除老数据
        conversionRateDao.deleteAll();
        //插入新数据
        if(CollectionUtils.isNotEmpty(searchList)){
            for(ConversionRateVO tmp: searchList){
                ConversionRate orderCounts = conversionRateDao.getOrderCount(tmp);
                ConversionRate newOrderCounts = conversionRateDao.getNewOrderCount(tmp);
                tmp.setIsRepeact(1);
                ConversionRate repeatOrderCounts = conversionRateDao.getRepeatOrderCount(tmp);
//                ConversionRate newRepeatOrderCounts = conversionRateDao.getNewOrderCount(tmp);
                tmp.setIsRepeact(null);
                tmp.setIsPay(1);
                ConversionRate newOrderPayCounts = conversionRateDao.getNewOrderCount(tmp);
                ConversionRate orderPayCounts = conversionRateDao.getOrderCount(tmp);
                tmp.setIsRepeact(1);
                ConversionRate repeatOrderPayCounts = conversionRateDao.getRepeatOrderCount(tmp);
                ConversionRate conversionRate = new ConversionRate();
                conversionRate.setName(tmp.getName());
                conversionRate.setTimeBucket(tmp.getTimeBucket());
                conversionRate.setOrders(orderCounts != null && orderCounts.getOrders() != null ? orderCounts.getOrders() : 0);
                conversionRate.setPeoples(orderPayCounts != null && orderPayCounts.getPeoples() != null ? orderPayCounts.getPeoples() : 0);
                conversionRate.setPays(orderPayCounts != null && orderPayCounts.getPeoples() != null ? orderPayCounts.getPeoples(): 0);
                conversionRate.setAmount(orderPayCounts != null && orderPayCounts.getAmount() != null ? orderPayCounts.getAmount() : new BigDecimal("0.00"));
                conversionRate.setNewOrders(newOrderCounts != null && newOrderCounts.getNewPeoples() != null ? newOrderCounts.getNewPeoples() : 0);
                // 新人数
                ConversionRate newPeopleCounts = conversionRateDao.getNewPeopleCount(tmp);
                conversionRate.setNewPeoples(newPeopleCounts != null && newPeopleCounts.getNewPeoples() != null ? newPeopleCounts.getNewPeoples() : 0);
                conversionRate.setNewAmount(newOrderCounts != null && newOrderCounts.getNewAmount() != null ? newOrderCounts.getNewAmount() : new BigDecimal("0.00"));
                conversionRate.setRepeatOrders(repeatOrderCounts!= null && repeatOrderCounts.getPeoples() != null ? repeatOrderCounts.getPeoples() : 0);
                conversionRate.setRepeatPays(repeatOrderPayCounts != null && repeatOrderPayCounts.getPeoples() != null ? repeatOrderPayCounts.getPeoples() : 0);
                conversionRate.setNewPays(newOrderPayCounts != null &&  newOrderPayCounts.getNewPeoples() != null ? newOrderPayCounts.getNewPeoples() : 0);
                if(conversionRate.getPeoples() != null && conversionRate.getPeoples().intValue() > 0){
                    conversionRate.setRepeatOrderRate(new BigDecimal(conversionRate.getRepeatOrders() * 1.0/ conversionRate.getPeoples()));
                } else {
                    conversionRate.setRepeatOrderRate(new BigDecimal("0.00"));
                }
                if(conversionRate.getPays() != null && conversionRate.getPays().intValue() > 0){
                    conversionRate.setRepeatPayRate(new BigDecimal(conversionRate.getRepeatPays() * 1.0 / conversionRate.getPays()));
                } else {
                    conversionRate.setRepeatPayRate(new BigDecimal("0.00"));
                }
                if(newOrderCounts != null && conversionRate.getNewPeoples() > 0){
                    conversionRate.setNewOrderRate(new BigDecimal(conversionRate.getNewOrders() * 1.0 / conversionRate.getNewPeoples()));
                    conversionRate.setNewPayRate(new BigDecimal(conversionRate.getNewPays() * 1.0 / conversionRate.getNewPeoples()));
                } else {
                    conversionRate.setNewOrderRate(new BigDecimal("0.00"));
                    conversionRate.setNewPayRate(new BigDecimal("0.00"));
                }
                conversionRate.setCreateTime(new Date());
                conversionRate.setUpdateTime(new Date());
                conversionRateDao.create(conversionRate);
            }
        }
    }

    /**
     * 获取需要统计的时间段
     * @return
     */
    private List<ConversionRateVO> getDateTimeList(){
        List<ConversionRateVO> searchList = new ArrayList<>();
        try {
            // 昨天
            ConversionRateVO yesterday = new ConversionRateVO();
            String beginTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -1, false), DateUtils.DATE_PATTERN);
            String endTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -1, false), DateUtils.DATE_PATTERN);
            yesterday.setBeginTime(beginTime + " 00:00:00");
            yesterday.setEndTime(endTime + " 23:59:59");
            yesterday.setName("昨天");
            yesterday.setTimeBucket(beginTime);
            searchList.add(yesterday);
            // 三日
            ConversionRateVO threeDays = new ConversionRateVO();
            beginTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -3, false), DateUtils.DATE_PATTERN);
            endTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -1, false), DateUtils.DATE_PATTERN);
            threeDays.setBeginTime(beginTime + " 00:00:00");
            threeDays.setEndTime(endTime + " 23:59:59");
            threeDays.setName("三日");
            threeDays.setTimeBucket(beginTime + "至" + endTime);
            searchList.add(threeDays);
            // 七日
            ConversionRateVO sevenDays = new ConversionRateVO();
            beginTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -7, false), DateUtils.DATE_PATTERN);
            endTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -1, false), DateUtils.DATE_PATTERN);
            sevenDays.setBeginTime(beginTime + " 00:00:00");
            sevenDays.setEndTime(endTime + " 23:59:59");
            sevenDays.setName("七日");
            sevenDays.setTimeBucket(beginTime + "至" + endTime);
            searchList.add(sevenDays);
            // 本周
            ConversionRateVO currentWeek = new ConversionRateVO();
            beginTime = DateUtils.dateFormat(DateUtils.getBeginDayOfWeek(), DateUtils.DATE_PATTERN);
            endTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -1, false), DateUtils.DATE_PATTERN);
            currentWeek.setBeginTime(beginTime + " 00:00:00");
            currentWeek.setEndTime(endTime + " 23:59:59");
            currentWeek.setName("本周");
            currentWeek.setTimeBucket(beginTime + "至" + endTime);
            searchList.add(currentWeek);

            // 本月
            ConversionRateVO currentMonth = new ConversionRateVO();
            beginTime = DateUtils.dateFormat(DateUtils.minDateOfMonth(DateUtils.dateAddMonths(null, 0)), DateUtils.DATE_PATTERN);
            endTime = DateUtils.dateFormat(DateUtils.dateAdd(null, -1, false), DateUtils.DATE_PATTERN);
            currentMonth.setBeginTime(beginTime + " 00:00:00");
            currentMonth.setEndTime(endTime + " 23:59:59");
            currentMonth.setName("本月");
            currentMonth.setTimeBucket(beginTime + "至" + endTime);
            searchList.add(currentMonth);

            // 三月
            ConversionRateVO threeMonth = new ConversionRateVO();
            beginTime = DateUtils.dateFormat(DateUtils.minDateOfMonth(DateUtils.dateAddMonths(null, -3)), DateUtils.DATE_PATTERN);
            endTime = DateUtils.dateFormat(DateUtils.maxDateOfMonth(DateUtils.dateAddMonths(null, -1)), DateUtils.DATE_PATTERN);
            threeMonth.setBeginTime(beginTime + " 00:00:00");
            threeMonth.setEndTime(endTime + " 23:59:59");
            threeMonth.setName("三月");
            threeMonth.setTimeBucket(beginTime + "至" + endTime);
            searchList.add(threeMonth);

            // 半年
            ConversionRateVO halfYear = new ConversionRateVO();
            beginTime = DateUtils.dateFormat(DateUtils.minDateOfMonth(DateUtils.dateAddMonths(null, -6)), DateUtils.DATE_PATTERN);
            endTime = DateUtils.dateFormat(DateUtils.maxDateOfMonth(DateUtils.dateAddMonths(null, -1)), DateUtils.DATE_PATTERN);
            halfYear.setBeginTime(beginTime + " 00:00:00");
            halfYear.setEndTime(endTime + " 23:59:59");
            halfYear.setName("半年");
            halfYear.setTimeBucket(beginTime + "至" + endTime);
            searchList.add(halfYear);

            // 一年
            ConversionRateVO OneYear = new ConversionRateVO();
            beginTime = DateUtils.dateFormat(DateUtils.minDateOfMonth(DateUtils.dateAddMonths(null, -12)), DateUtils.DATE_PATTERN);
            endTime = DateUtils.dateFormat(DateUtils.maxDateOfMonth(DateUtils.dateAddMonths(null, -1)), DateUtils.DATE_PATTERN);
            OneYear.setBeginTime(beginTime + " 00:00:00");
            OneYear.setEndTime(endTime + " 23:59:59");
            OneYear.setName("一年");
            OneYear.setTimeBucket(beginTime + "至" + endTime);
            searchList.add(OneYear);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchList;
    }
}
