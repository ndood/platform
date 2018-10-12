package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.RoomBlacklistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomBlacklistDao;
import com.fulu.game.core.entity.RoomBlacklist;
import com.fulu.game.core.service.RoomBlacklistService;

import java.util.Date;
import java.util.List;


@Service
public class RoomBlacklistServiceImpl extends AbsCommonService<RoomBlacklist,Integer> implements RoomBlacklistService {

    @Autowired
	private RoomBlacklistDao roomBlacklistDao;


    /**
     * 添加用户到黑名单
     * @param userId
     * @param roomNo
     */
    public void create(Integer userId,String roomNo){
        RoomBlacklist roomBlacklist = findByUserAndRoomNo(userId,roomNo);
        if(roomBlacklist!=null){
            return;
        }
        roomBlacklist = new RoomBlacklist();
        roomBlacklist.setRoomNo(roomNo);
        roomBlacklist.setUserId(userId);
        roomBlacklist.setCreateTime(new Date());
        create(roomBlacklist);
    }

    /**
     * 移出黑名单
     * @param userId
     * @param roomNo
     */
    public void delete(Integer userId,String roomNo){
        RoomBlacklist roomBlacklist = findByUserAndRoomNo(userId,roomNo);
        if(roomBlacklist!=null){
            deleteById(roomBlacklist.getId());
        }
    }

    /**
     * 查询用户是否在黑名单
     * @param userId
     * @param roomNo
     * @return
     */
    public RoomBlacklist findByUserAndRoomNo(Integer userId,String roomNo){
        RoomBlacklistVO param = new RoomBlacklistVO();
        param.setUserId(userId);
        param.setRoomNo(roomNo);
        List<RoomBlacklist> roomBlacklists = roomBlacklistDao.findByParameter(param);
        if(roomBlacklists.isEmpty()){
            return null;
        }
        return roomBlacklists.get(0);
    }


    @Override
    public ICommonDao<RoomBlacklist, Integer> getDao() {
        return roomBlacklistDao;
    }
	
}
