package com.fulu.game.core.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.exception.TechLevelException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.TechLevelVO;
import com.fulu.game.core.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.TechLevelDao;
import com.fulu.game.core.entity.TechLevel;
import com.fulu.game.core.service.TechLevelService;

import java.util.Date;
import java.util.List;


@Service
public class TechLevelServiceImpl extends AbsCommonService<TechLevel,Integer> implements TechLevelService {

    @Autowired
	private TechLevelDao techLevelDao;
    @Autowired
    private AdminService adminService;



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
        Admin admin = adminService.getCurrentUser();
        techLevelVO.setOperatorId(admin.getId());
        techLevelVO.setOperatorName(admin.getName());
        techLevelVO.setUpdateTime(new Date());
        if(techLevelVO != null && techLevelVO.getId() != null && techLevelVO.getId().intValue() > 0){
            techLevelDao.update(techLevelVO);
        } else {
            techLevelVO.setCreateTime(new Date());
            techLevelVO.setIsDel(0);
            techLevelDao.create(techLevelVO);
        }
    }
}
