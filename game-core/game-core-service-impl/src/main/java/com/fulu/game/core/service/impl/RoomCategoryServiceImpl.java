package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.RoomCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomCategoryDao;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.service.RoomCategoryService;

import java.util.ArrayList;
import java.util.List;


@Service
public class RoomCategoryServiceImpl extends AbsCommonService<RoomCategory,Integer> implements RoomCategoryService {

    @Autowired
	private RoomCategoryDao roomCategoryDao;



    @Override
    public ICommonDao<RoomCategory, Integer> getDao() {
        return roomCategoryDao;
    }



    @Override
    public List<RoomCategory> findActivateRoomByPid(int pid) {
        RoomCategoryVO param = new RoomCategoryVO();
        param.setPid(pid);
        param.setIsActivate(true);
        List<RoomCategory> categoryList = roomCategoryDao.findByParameter(param);
        if(categoryList.isEmpty()){
            return new ArrayList<>();
        }
        return categoryList;
    }



    @Override
    public List<RoomCategory> findActivateRoomCategory() {
        List<RoomCategory> rootCategorys = findActivateRoomByPid(0);
        if(rootCategorys.isEmpty()){
            return new ArrayList<>();
        }
        RoomCategory rootCategory = rootCategorys.get(0);
        return findActivateRoomByPid(rootCategory.getId());
    }



    public RoomCategory getRootCategory(){
        RoomCategoryVO param = new RoomCategoryVO();
        param.setPid(0);
        List<RoomCategory> categoryList = roomCategoryDao.findByParameter(param);
        if(categoryList.isEmpty()){
            throw new IllegalArgumentException("没有设置根分类!");
        }
        return categoryList.get(0);
    }


    public List<RoomCategory> findByPid(int pid){
        RoomCategoryVO param = new RoomCategoryVO();
        param.setPid(pid);
        List<RoomCategory> categoryList = roomCategoryDao.findByParameter(param);
        return categoryList;
    }


    @Override
    public List<RoomCategory> list() {
        RoomCategory rootCategory = getRootCategory();
        return findByPid(rootCategory.getId());
    }


}
