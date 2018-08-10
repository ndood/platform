package com.fulu.game.core.service.impl;


import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.WechatFormidDao;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.entity.vo.WechatFormidVO;
import com.fulu.game.core.service.WechatFormidService;
import com.github.pagehelper.PageHelper;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class WechatFormidServiceImpl extends AbsCommonService<WechatFormid, Integer> implements WechatFormidService {

    @Autowired
    private WechatFormidDao wechatFormidDao;
    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;

    @Override
    public ICommonDao<WechatFormid, Integer> getDao() {
        return wechatFormidDao;
    }


    @Override
    public List<WechatFormid> findInSevenDaysFormIdByUser(Integer userId,int platform) {
        WechatFormidVO params = new WechatFormidVO();
        params.setUserId(userId);
        params.setPlatform(platform);
        List<WechatFormid> formids = wechatFormidDao.findByParameter(params);
        List<WechatFormid> availableFormIds = new ArrayList<>();
        List<WechatFormid> notAvailableFormIds = new ArrayList<>();
        for (WechatFormid formid : formids) {
            long day = DateUtil.between(formid.getCreateTime(), new Date(), DateUnit.DAY);
            if (day < 7) {
                availableFormIds.add(formid);
            } else {
                notAvailableFormIds.add(formid);
            }
        }
        if (notAvailableFormIds.size() > 0) {
            springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    deleteNotAvailableFormIds(notAvailableFormIds.toArray(new WechatFormid[]{}));
                }
            });
        }
        return availableFormIds;
    }



    public void deleteNotAvailableFormIds(WechatFormid... wechatFormid) {
        if (wechatFormid.length > 0) {
            for (WechatFormid w : wechatFormid) {
                log.info("删除formId:wechatFormid:{}",wechatFormid);
                deleteById(w.getId());
            }
        }
    }


    public List<WechatFormidVO> findByUserIds(int platform,
                                              List<Integer> userIds,
                                              int offset,
                                              int size){
        return wechatFormidDao.findByUserIds(platform,userIds,offset,size);
    }


    public void deleteByExpireTime(Date date){
        wechatFormidDao.deleteByExpireTime(date);
    }



    @Override
    public void deleteFormIds(String... fromIds) {
        wechatFormidDao.deleteFormIds(Arrays.asList(fromIds));
    }

}
