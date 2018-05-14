package com.fulu.game.core.service.impl;


import com.fulu.game.common.cache.AbsGuavaCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SysConfigDao;
import com.fulu.game.core.entity.SysConfig;
import com.fulu.game.core.service.SysConfigService;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class SysConfigServiceImpl extends AbsGuavaCache<String,List<SysConfig>> implements SysConfigService {


    private static final String ALL= "ALL"; //查询全部


    @Autowired
	private SysConfigDao sysConfigDao;



    @Override
    protected void initCacheFields() {
        this.refreshDuration = 30;
        this.refreshTimeUnitType = TimeUnit.SECONDS;
        this.cacheMaximumSize=1;
    }

    @Override
    protected List<SysConfig> getValueWhenExpire(String key) throws Exception {
        if(ALL.equals(key)){
            return sysConfigDao.findAll();
        }
        return null;
    }

    @Override
    public List<SysConfig> getValue(String key) {
        try {
            return fetchDataFromCache(key);
        }catch (Exception e){
            log.error("缓存失效:",e);
            return sysConfigDao.findAll();
        }
    }

    @Autowired
    public List<SysConfig> findAll(){
        return getValue(ALL);
    }
}
