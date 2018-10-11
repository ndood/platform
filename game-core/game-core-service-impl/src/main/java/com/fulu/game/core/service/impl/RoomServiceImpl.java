package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.RoomRoleTypeEnum;
import com.fulu.game.common.exception.RoomException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RoomDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.RoomMicVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.entity.vo.UserChatRoomVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
@Slf4j
public class RoomServiceImpl extends AbsCommonService<Room, Integer> implements RoomService {

    @Autowired
    private RoomDao roomDao;
    @Autowired
    private RoomCategoryService roomCategoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomManageService roomManageService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private RoomCollectService roomCollectService;

    @Override
    public ICommonDao<Room, Integer> getDao() {
        return roomDao;
    }


    @Override
    public PageInfo<RoomVO> findUsableRoomsByHot(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "sort desc");
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setIsHot(true);
        param.setIsShow(true);
        List<Room> roomList = roomDao.findByParameter(param);
        List<RoomVO> result = new ArrayList<>();
        for (Room room : roomList) {
            RoomVO roomVO = room2VO(room);
            result.add(roomVO);
        }
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }

    @Override
    public PageInfo<RoomVO> list(int pageNum, int pageSize, String name) {
        PageHelper.startPage(pageNum, pageSize, "sort desc");
        RoomVO param = new RoomVO();
        param.setName(name);
        List<Room> roomList = roomDao.findByParameter(param);
        PageInfo page = new PageInfo(roomList);
        List<RoomVO> roomVOList = new ArrayList<>();
        for (Room room : roomList) {
            RoomVO roomVO = room2VO(room);
            roomVOList.add(roomVO);
        }
        page.setList(roomVOList);
        return page;
    }


    public PageInfo<RoomVO> findCollectRoomByUser(int pageNum, int pageSize, int userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Room> roomList = roomDao.findCollectRoomByUser(userId);
        List<RoomVO> result = new ArrayList<>();
        for (Room room : roomList) {
            RoomVO roomVO = room2VO(room);
            result.add(roomVO);
        }
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }


    @Override
    public PageInfo<RoomVO> findUsableRoomsByRoomCategory(int pageNum, int pageSize, int roomCategoryId) {
        PageHelper.startPage(pageNum, pageSize, "sort desc");
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setIsShow(true);
        param.setRoomCategoryId(roomCategoryId);
        List<Room> roomList = roomDao.findByParameter(param);
        List<RoomVO> result = new ArrayList<>();
        for (Room room : roomList) {
            RoomVO roomVO = room2VO(room);
            result.add(roomVO);
        }
        result.sort((RoomVO r1, RoomVO r2) -> r1.getPeople().compareTo(r2.getPeople()));
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }


    @Override
    public RoomVO findByOwner(int userId) {
        Room room = findByUser(userId);
        if (room == null) {
            return null;
        }
        RoomVO roomVO = new RoomVO();
        BeanUtil.copyProperties(findByUser(userId), roomVO);
        return roomVO;
    }


    @Override
    public Room findByUser(int userId) {
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setUserId(userId);
        List<Room> roomList = roomDao.findByParameter(param);
        if (roomList.isEmpty()) {
            return null;
        }
        return roomList.get(0);
    }

    @Override
    public RoomVO save(RoomVO roomVO) {
        roomVO.setUpdateTime(new Date());
        if (roomVO.getId() == null) {
            User user = userService.findByMobile(roomVO.getOwnerMobile());
            if (user == null) {
                throw new ServiceErrorException("手机号不存在!");
            }
            roomVO.setUserId(user.getId());
            if (roomVO.getVirtualPeople() == null) {
                roomVO.setVirtualPeople(0);
            }
            roomVO.setIsShow(true);
            roomVO.setIsLock(false);
            roomVO.setRoomNo(generateRoomNo());
            roomVO.setCreateTime(new Date());
            create(roomVO);
            //创建管理员
            roomManageService.createManage(RoomRoleTypeEnum.OWNER, user.getId(), roomVO.getRoomNo());
        } else {
            update(roomVO);
        }
        return roomVO;
    }


    public Room findByRoomNo(String roomNo) {
        if (roomNo == null) {
            return null;
        }
        RoomVO param = new RoomVO();
        param.setRoomNo(roomNo);
        List<Room> roomList = roomDao.findByParameter(param);
        if (roomList.isEmpty()) {
            return null;
        }
        return roomList.get(0);
    }


    /**
     * 生成房间号
     *
     * @return
     */
    private String generateRoomNo() {
        String roomNo = GenIdUtil.GetRoomNo();
        if (findByRoomNo(roomNo) == null) {
            return roomNo;
        } else {
            return generateRoomNo();
        }
    }


    /**
     * 房间实体转换成VO
     *
     * @param room
     * @return
     */
    public RoomVO room2VO(Room room) {
        RoomVO roomVO = new RoomVO();
        BeanUtil.copyProperties(room, roomVO);
        //设置房间分类
        RoomCategory roomCategory = roomCategoryService.findById(room.getRoomCategoryId());
        roomVO.setRoomCategoryName(roomCategory.getName());
        //计算聊天室人数
        Long realPeople = getChatRoomPeople(room.getRoomNo());
        Integer virtualPeople = room.getVirtualPeople();
        roomVO.setPeople(realPeople + virtualPeople);
        return roomVO;
    }



    public UserChatRoomVO userEnterChatRoom(User user, String roomNo, String password) {
        Room room = checkRoomExists(roomNo);
        if (room.getIsLock()) {
            if (password == null || !password.equals(room.getPassword())) {
                throw new RoomException(RoomException.ExceptionCode.ROOM_PASSWORD_ERROR);
            }
        }
        redisOpenService.setForAdd(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo), user.getId());
        return setUserRoomInfo(user, roomNo);
    }


    /**
     * 更新用户进入的房间信息
     * @param user
     * @param roomNo
     * @return
     */
    public UserChatRoomVO setUserRoomInfo(User user, String roomNo) {
        Room room = checkRoomExists(roomNo);

        //组装用户信息
        UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
        userChatRoomVO.setUserId(user.getId());
        userChatRoomVO.setNickname(user.getNickname());
        userChatRoomVO.setGender(user.getGender());
        userChatRoomVO.setAge(user.getAge());
        userChatRoomVO.setHeadPortraitsUrl(user.getHeadPortraitsUrl());
        userChatRoomVO.setOrderRate(new BigDecimal(100));
        userChatRoomVO.setSatisfy(new BigDecimal(5.0));
        userChatRoomVO.setRoomNo(roomNo);
        userChatRoomVO.setRoomName(room.getName());
        userChatRoomVO.setRoomIcon(room.getIcon());
        userChatRoomVO.setNotice(room.getNotice());
        userChatRoomVO.setSlogan(room.getSlogan());
        userChatRoomVO.setVirtualPeople(room.getVirtualPeople() == null ? 0 : room.getVirtualPeople());
        userChatRoomVO.setPeople(userChatRoomVO.getVirtualPeople() + getChatRoomPeople(roomNo));
        //todo 计算用户在房间送出礼物数量
        userChatRoomVO.setGiftPrice(new BigDecimal(0));
        //用户身份
        RoomManage roomManage = roomManageService.findByUserAndRoomNo(user.getId(), roomNo);
        if (roomManage != null) {
            userChatRoomVO.setRoomRole(roomManage.getRole());
        }
        //是否收藏
        RoomCollect roomCollect = roomCollectService.findByRoomAndUser(roomNo, user.getId());
        userChatRoomVO.setIsCollect(roomCollect != null);



        //存储
        Map<String, Object> userMap = BeanUtil.beanToMap(userChatRoomVO);
        redisOpenService.hset(RedisKeyEnum.CHAT_ROOM_ONLINE_USER_INFO.generateKey(user.getId()), userMap, true);
        return userChatRoomVO;
    }


    /**
     * 更新用户在聊天室信息
     * @param userChatRoomVO
     * @return
     */
    public UserChatRoomVO setUserRoomInfo(UserChatRoomVO userChatRoomVO) {
        Map<String, Object> userMap = BeanUtil.beanToMap(userChatRoomVO);
        redisOpenService.hset(RedisKeyEnum.CHAT_ROOM_ONLINE_USER_INFO.generateKey(userChatRoomVO.getUserId()), userMap, true);
        return userChatRoomVO;
    }


    /**
     * 获取用户在聊天室信息
     * @return
     */
    public UserChatRoomVO getUserRoomInfo(Integer userId) {
        Map<String, Object> userMap = redisOpenService.hget(RedisKeyEnum.CHAT_ROOM_ONLINE_USER_INFO.generateKey(userId));
        if (userMap == null) {
            return null;
        }
        return BeanUtil.mapToBean(userMap, UserChatRoomVO.class, true);
    }




    /**
     * 用户退出房间
     * @param userId
     * @param roomNo
     * @return
     */
    public long userQuitChatRoom(Integer userId, String roomNo) {
        //删除用户聊天室在线信息
        redisOpenService.delete(RedisKeyEnum.CHAT_ROOM_ONLINE_USER_INFO.generateKey(userId));
        //删除用户聊天室状态
        return redisOpenService.setForDel(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo), userId);
    }

    /**
     * 聊天室马甲用户列表
     * @return
     */
    @Override
    public List<UserChatRoomVO> roomMangerList(String roomNo){
        List<RoomManage> roomManages =  roomManageService.findByRoomNo(roomNo);
        List<UserChatRoomVO> list = new ArrayList<>();
        for(RoomManage roomManage : roomManages){
            User user = userService.findById(roomManage.getUserId());
            //组装用户信息
            UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
            userChatRoomVO.setUserId(user.getId());
            userChatRoomVO.setNickname(user.getNickname());
            userChatRoomVO.setGender(user.getGender());
            userChatRoomVO.setAge(user.getAge());
            userChatRoomVO.setHeadPortraitsUrl(user.getHeadPortraitsUrl());
            userChatRoomVO.setRoomNo(roomNo);
            userChatRoomVO.setRoomRole(roomManage.getRole());
            //查询用户所在房间的麦位
            Integer micIndex = getUserMicIndex(roomNo,user.getId());
            if(micIndex!=null){
                userChatRoomVO.setMicIndex(micIndex);
            }
            //todo 计算用户在房间送出礼物数量
            userChatRoomVO.setGiftPrice(new BigDecimal(0));
            list.add(userChatRoomVO);
        }
        return list;
    }

    @Override
    public List<UserChatRoomVO> getOnlineUser(String roomNo) {
        List<UserChatRoomVO> userChatRoomVOS = new ArrayList<>();
        if (!redisOpenService.hasKey(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo))) {
            return userChatRoomVOS;
        }
        //获取所有在线用户ID
        Set<Integer> userIds = redisOpenService.setForAll(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo));
        for(Integer userId : userIds){
            UserChatRoomVO userChatRoomVO =getUserRoomInfo(userId);
            if(userChatRoomVO!=null){
                //查询用户所在房间的麦位
                Integer micIndex = getUserMicIndex(roomNo,userId);
                if(micIndex!=null){
                    userChatRoomVO.setMicIndex(micIndex);
                }
                userChatRoomVOS.add(userChatRoomVO);
            }else{
                //同步删除在房间用户
                redisOpenService.setForDel(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo), userId);
            }
        }
        return userChatRoomVOS;
    }

    /**
     * 查询用户在几号麦位上
     * @return
     */
    public Integer getUserMicIndex(String roomNo,Integer userId){
        //所有麦位信息
        List<RoomMicVO> roomMicVOList = roomMicAll(roomNo);
        for(RoomMicVO vo : roomMicVOList){
            RoomMicVO.MicUser micUser = vo.getMicUser();
            if(micUser!=null&&micUser.getUserId().equals(userId)){
                return vo.getIndex();
            }
        }
        return null;
    }




    @Override
    public List<RoomMicVO> roomMicAll(String roomNo) {
        checkRoomExists(roomNo);

        if (!redisOpenService.hasKey(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo))) {
            List<RoomMicVO> roomMicVOList = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                RoomMicVO roomMicVO = new RoomMicVO();
                roomMicVO.setIndex(i);
                roomMicVO.setStatus(RoomMicVO.MicStatus.OPEN.getStatus());
                roomMicVOList.add(roomMicVO);
            }
            redisOpenService.listForInit(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo), roomMicVOList);
            return roomMicVOList;
        }
        return  redisOpenService.listForAll(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo));
    }


    @Override
    public RoomMicVO roomMicHoldUp(String roomNo, int index, int userId) {
        log.info("用户上麦:roomNo:{},index:{},userId:{}",roomNo,index,userId);
        if (index < 0 || index > 8) {
            throw new RoomException(RoomException.ExceptionCode.ROOM_MIC_INDEX_ERROR);
        }
        checkRoomExists(roomNo);

        //如果麦位不存在初始化麦位
        if(!redisOpenService.hasKey(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo))){
            roomMicAll(roomNo);
        }
        //检查用户是否在该房间
        checkRoomUserOnline(roomNo,userId);

        //查询该用户是否已经在麦位上了
        List<RoomMicVO> roomMicList = redisOpenService.listForAll(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo));
        for (RoomMicVO micVO : roomMicList) {
            if (micVO.getMicUser() != null && micVO.getMicUser().getUserId().equals(userId) && !micVO.getIndex().equals(index)) {
                throw new RoomException(RoomException.ExceptionCode.ROOM_MIC_INDEX_EXIST);
            }
        }
        RoomMicVO roomMicVO = redisOpenService.listGetForIndex(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo), index);
        if (roomMicVO == null) {
            roomMicVO = new RoomMicVO();
            roomMicVO.setStatus(RoomMicVO.MicStatus.OPEN.getStatus());
            roomMicVO.setIndex(index);
        }

        User user = userService.findById(userId);
        RoomMicVO.MicUser micUser = new RoomMicVO.MicUser();
        micUser.setUserId(user.getId());
        micUser.setGender(user.getGender());
        micUser.setHeadPortraitsUrl(user.getHeadPortraitsUrl());
        micUser.setAge(user.getAge());
        micUser.setNickname(user.getNickname());
        //查询用户身份
        RoomManage roomManage = roomManageService.findByUserAndRoomNo(userId, roomNo);
        if (roomManage != null) {
            micUser.setRoomRole(roomManage.getRole());
        }
        roomMicVO.setMicUser(micUser);
        redisOpenService.listSetForIndex(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo), index, roomMicVO);
        return roomMicVO;
    }


    @Override
    public RoomMicVO roomMicStatus(String roomNo, int index, int status) {
        log.info("更改麦位状态:roomNo:{},index:{},status:{}",roomNo,index,status);

        if (index < 0 || index > 8) {
            throw new RoomException(RoomException.ExceptionCode.ROOM_MIC_INDEX_ERROR);
        }
        checkRoomExists(roomNo);
        RoomMicVO roomMicVO = redisOpenService.listGetForIndex(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo), index);
        //下麦用户
        if (Integer.valueOf(-1).equals(status)) {
            roomMicVO.setMicUser(null);
        } else {//设置麦位状态
            roomMicVO.setStatus(status);
        }
        redisOpenService.listSetForIndex(RedisKeyEnum.CHAT_ROOM_MIC.generateKey(roomNo), index, roomMicVO);
        return roomMicVO;
    }



    @Override
    public List<UserChatRoomVO> roomMicUpList(String roomNo, Integer type) {
        checkRoomExists(roomNo);
        if(type>2||type<1){
            throw new RoomException(RoomException.ExceptionCode.ROOM_MIC_UP_LIST_ERROR);
        }

        List<UserChatRoomVO> userChatRoomVOS = new ArrayList<>();
        Set<Integer> userIds =  redisOpenService.zsetForAll(RedisKeyEnum.CHAT_ROOM_MIC_UP_LIST.generateKey(roomNo,type),true);
        for(Integer userId : userIds){
            UserChatRoomVO userChatRoomVO =getUserRoomInfo(userId);
            if(userChatRoomVO!=null){
                //查询用户所在房间的麦位
                Integer micIndex = getUserMicIndex(roomNo,userId);
                if(micIndex!=null){
                    userChatRoomVO.setMicIndex(micIndex);
                }
                userChatRoomVOS.add(userChatRoomVO);
            }else{
                //同步删除在房间用户
                redisOpenService.setForDel(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo), userId);
            }
        }
        return userChatRoomVOS;
    }



    @Override
    public long roomMicUp(String roomNo, Integer type,int userId){
        log.info("用户上麦roomNo:{},type:{},userId:{}",roomNo,type,userId);
        checkRoomExists(roomNo);
        if(type>2||type<1){
            throw new RoomException(RoomException.ExceptionCode.ROOM_MIC_UP_LIST_ERROR);
        }
        checkRoomUserOnline(roomNo,userId);
        redisOpenService.zsetForAdd(RedisKeyEnum.CHAT_ROOM_MIC_UP_LIST.generateKey(roomNo,type),Integer.valueOf(userId));
        return roomMicUpSize(roomNo,type);
    }



    @Override
    public long roomMicDown(String roomNo, Integer type, int userId){
        log.info("用户下麦roomNo:{},type:{},userId:{}",roomNo,type,userId);
        checkRoomExists(roomNo);
        if(type>2||type<1){
            throw new RoomException(RoomException.ExceptionCode.ROOM_MIC_UP_LIST_ERROR);
        }
        checkRoomUserOnline(roomNo,userId);
        redisOpenService.zsetForDel(RedisKeyEnum.CHAT_ROOM_MIC_UP_LIST.generateKey(roomNo,type),userId);
        return roomMicUpSize(roomNo,type);
    }


    /**
     * 麦序列表大小
      * @param roomNo
     * @param types
     * @return
     */
    public Map<Integer,Long> roomMicUpSize(String roomNo, List<Integer> types){
        if(types==null){
            return new HashMap<>();
        }
        Map<Integer,Long>  result = new HashMap<>();
        for(Integer type : types){
            result.put(type,roomMicUpSize(roomNo,type));
        }

        return result;
    }



    public long roomMicUpSize(String roomNo, Integer type){
        if(!redisOpenService.hasKey(RedisKeyEnum.CHAT_ROOM_MIC_UP_LIST.generateKey(roomNo,type))){
            return 0;
        }
        return redisOpenService.zsetForSize(RedisKeyEnum.CHAT_ROOM_MIC_UP_LIST.generateKey(roomNo,type));
    }


    /**
     * 检查用户是否在该房间
     */
    public UserChatRoomVO checkRoomUserOnline(String roomNo,int userId){
        UserChatRoomVO userChatRoomVO = getUserRoomInfo(userId);
        if(userChatRoomVO==null){
            throw new RoomException(RoomException.ExceptionCode.ROOM_USER_QUIT_EXCEPTION);
        }
        if(!userChatRoomVO.getRoomNo().equals(roomNo)){
            throw new RoomException(RoomException.ExceptionCode.ROOM_USER_QUIT_EXCEPTION);
        }
        return userChatRoomVO;
    }

    /**
     *
     * @param roomNo
     * @return
     */
    public Room checkRoomExists(String roomNo){
        Room room = findByRoomNo(roomNo);
        if (room == null) {
            throw new RoomException(RoomException.ExceptionCode.ROOM_NOT_EXIST);
        }
        return room;
    }


    /**
     * 获取聊天室真实人数
     * @return
     */
    private long getChatRoomPeople(String roomNo) {
        if (!redisOpenService.hasKey(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo))) {
            return 0;
        }
        return redisOpenService.setForSize(RedisKeyEnum.CHAT_ROOM_ONLINE_USER.generateKey(roomNo));
    }


}
