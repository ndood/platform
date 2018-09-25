package com.fulu.game.core.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.exception.TechLevelException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.TechLevelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.TechLevelDao;
import com.fulu.game.core.entity.TechLevel;
import com.fulu.game.core.service.TechLevelService;

import java.util.List;


@Service
public class TechLevelServiceImpl extends AbsCommonService<TechLevel,Integer> implements TechLevelService {

    @Autowired
	private TechLevelDao techLevelDao;



    @Override
    public ICommonDao<TechLevel, Integer> getDao() {
        return techLevelDao;
    }

    /**
     * 获取技能等级列表
     *
     * @return
     */
    @Override
    public List<TechLevel> list() {
        TechLevelVO techLevelVO = new TechLevelVO();
        techLevelVO.setIsDel(0);
        return techLevelDao.findByParameter(techLevelVO);
    }

    /**
     * 保存技能等级信息
     *
     * @param techLevelVO
     */
    @Override
    public void save(TechLevelVO techLevelVO) {
        TechLevelVO params = new TechLevelVO();
        params.setId(techLevelVO.getId());
        params.setName(techLevelVO.getName());
        List<TechLevel> list = techLevelDao.findByParameter(techLevelVO);
        if(CollectionUtil.isNotEmpty(list)){
            throw new TechLevelException(TechLevelException.ExceptionCode.NAME_EXIST);
        }
        if(techLevelVO != null && techLevelVO.getId() != null && techLevelVO.getId().intValue() > 0){
            techLevelDao.update(techLevelVO);
        } else {
            techLevelDao.create(techLevelVO);
        }
    }
}
