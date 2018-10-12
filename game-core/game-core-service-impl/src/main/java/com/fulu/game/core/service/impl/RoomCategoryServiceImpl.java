package com.fulu.game.core.service.impl;


import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.RoomCategoryVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.RoomService;
import com.github.pagehelper.PageInfo;
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
    @Autowired
    private RoomService roomService;

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


    @Override
    public List<RoomCategoryVO> getRoomListCategory(List<RoomCategory> roomCategoryList, Integer userId) {
        if(roomCategoryList.isEmpty()){
            return new ArrayList<>();
        }
        List<RoomCategoryVO> result  = CollectionUtil.copyNewCollections(roomCategoryList,RoomCategoryVO.class);
        for(RoomCategoryVO roomCategoryVO : result){
            List<RoomVO> roomVOList = null;
            if(Integer.valueOf(999).equals(roomCategoryVO.getId())){//收藏列表
                if(userId==null){
                    roomVOList = new ArrayList<>();
                }else{
                    PageInfo<RoomVO> roomVOPage = roomService.findCollectRoomByUser(1,10,userId);
                    roomVOList = roomVOPage.getList();
                }
            }else if(Integer.valueOf(998).equals(roomCategoryVO.getId())){//热门列表
                PageInfo<RoomVO> roomVOPage = roomService.findUsableRoomsByHot(1, 10);
                roomVOList = roomVOPage.getList();
            }else{
                PageInfo<RoomVO> roomVOPage = roomService.findUsableRoomsByRoomCategory(1,10,roomCategoryVO.getId());
                roomVOList = roomVOPage.getList();
            }
            roomCategoryVO.setRoomList(roomVOList);
        }
        return result;
    }


}
