package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.common.enums.RoomRoleTypeEnum;
import com.fulu.game.common.exception.RoomException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.RoomCategoryVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.entity.vo.UserChatRoomVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/api/v1/room")
public class RoomController extends BaseController {

    @Autowired
    private RoomCategoryService roomCategoryService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private RoomManageService roomManageService;

    /**
     * 房间banner
     * @return
     */
    @PostMapping("/banner")
    public Result list() {
        List<Banner> bannerList = bannerService.findByPlatformType(PlatformBannerEnum.APP_CHAT_ROOM.getType());
        return Result.success().data(bannerList);
    }


    /**
     * 查找所有可用的房间分类
     *
     * @return
     */
    @RequestMapping("/category")
    public Result roomCategoryAll() {
        List<RoomCategory> list = roomCategoryService.findActivateRoomCategory();
        RoomCategory collectRoomCategory = new RoomCategory();
        collectRoomCategory.setId(999);
        collectRoomCategory.setName("收藏");
        collectRoomCategory.setIcon("https://game-play.oss-cn-hangzhou.aliyuncs.com/img/Chatroom_collect_default%403x.png");
        collectRoomCategory.setSelectedIcon("https://game-play.oss-cn-hangzhou.aliyuncs.com/img/Chatroom_collect_selected%403x.png");
        collectRoomCategory.setIsActivate(true);
        collectRoomCategory.setSort(999);

        RoomCategory hotRoomCategory = new RoomCategory();
        hotRoomCategory.setId(998);
        hotRoomCategory.setName("热门");
        hotRoomCategory.setIcon("https://game-play.oss-cn-hangzhou.aliyuncs.com/img/Chatroom_hot_default%403x.png");
        hotRoomCategory.setSelectedIcon("https://game-play.oss-cn-hangzhou.aliyuncs.com/img/Chatroom_hot_selected%403x.png");
        hotRoomCategory.setIsActivate(true);
        hotRoomCategory.setSort(998);

        if (!list.isEmpty()) {
            list.add(collectRoomCategory);
            list.add(hotRoomCategory);
            list.sort((RoomCategory c1, RoomCategory c2) -> c2.getSort().compareTo(c1.getSort()));
        }
        User user = userService.getCurrentUser();
        List<RoomCategoryVO> result = roomCategoryService.getRoomListCategory(list, user.getId());
        return Result.success().data(result);
    }


    /**
     * 房间列表
     *
     * @return
     */
    @RequestMapping("/list")
    public Result roomCategoryCommon(@RequestParam(required = true) Integer pageNum,
                                     @RequestParam(required = true) Integer pageSize,
                                     @RequestParam(required = true) Integer roomCategoryId) {
        if (Integer.valueOf(999).equals(roomCategoryId)) {//收藏列表
            User user = userService.getCurrentUser();
            PageInfo<RoomVO> roomVOList = roomService.findCollectRoomByUser(pageNum, pageSize, user.getId());
            return Result.success().data(roomVOList);
        } else if (Integer.valueOf(998).equals(roomCategoryId)) {//热门列表
            PageInfo<RoomVO> roomVOList = roomService.findUsableRoomsByHot(pageNum, pageSize);
            return Result.success().data(roomVOList);
        } else {
            PageInfo<RoomVO> roomVOList = roomService.findUsableRoomsByRoomCategory(pageNum, pageSize, roomCategoryId);
            return Result.success().data(roomVOList);
        }
    }


    /**
     * 我的房间
     *
     * @return
     */
    @RequestMapping("/mine")
    public Result roomCategoryMine() {
        User user = userService.getCurrentUser();
        RoomVO roomVO = roomService.findByOwner(user.getId());
        return Result.success().data(roomVO);
    }


    /**
     * 房间设置
     *
     * @return
     */
    @RequestMapping("/setting/update")
    public Result roomSettingSave(@RequestParam(required = true) String roomNo,
                                  String icon,
                                  String name,
                                  Boolean isShow,
                                  Boolean isLock,
                                  String password,
                                  String notice,
                                  String slogan) {
        User user = userService.getCurrentUser();
        Room room = roomService.findByRoomNo(roomNo);
        if (room == null) {
            throw new RoomException(RoomException.ExceptionCode.ROOM_NOT_EXIST);
        }
        RoomManage roomManage = roomManageService.findByUserAndRoomNo(user.getId(), roomNo);
        if (roomManage == null) {
            throw new RoomException(RoomException.ExceptionCode.ROOM_UPDATE_NO_PERMISSIONS);
        }
        RoomVO roomVO = new RoomVO();
        roomVO.setId(room.getId());
        roomVO.setIcon(icon);
        roomVO.setName(name);
        roomVO.setIsShow(isShow);
        roomVO.setIsLock(isLock);
        roomVO.setPassword(password);
        roomVO.setNotice(notice);
        roomVO.setSlogan(slogan);
        roomService.save(roomVO);
        return Result.success().msg("房间信息设置成功!");
    }


    /**
     * 房间马甲设置
     *
     * @return
     */
    @RequestMapping("/role/add")
    public Result roomRoleAdd(@RequestParam(required = true) String roomNo,
                              @RequestParam(required = true) Integer userId,
                              @RequestParam(required = true) Integer role) {
        RoomRoleTypeEnum roleTypeEnum = RoomRoleTypeEnum.findByType(role);
        if (roleTypeEnum == null || roleTypeEnum.equals(RoomRoleTypeEnum.OWNER)) {
            throw new RoomException(RoomException.ExceptionCode.ROOM_MANAGER_NOT_MATCHING);
        }
        roomManageService.createManage(roleTypeEnum, userId, roomNo);
        return Result.success().msg("设置为" + roleTypeEnum.getMsg() + "成功!");
    }


    /**
     * 房间马甲设置
     *
     * @return
     */
    @RequestMapping("/role/del")
    public Result roomRoleDel(@RequestParam(required = true) String roomNo,
                              @RequestParam(required = true) Integer userId) {
        roomManageService.deleteManage(userId, roomNo);
        return Result.success().msg("解除成功!");
    }

    /**
     * 查询聊天室信息
     * @param roomNo
     * @return
     */
    @RequestMapping("/info")
    public Result roomInfo(@RequestParam(required = true) String roomNo) {
        Room room = roomService.findByRoomNo(roomNo);
        return Result.success().data(roomService.room2VO(room));
    }

    /**
     * 进入聊天室
     * @param roomNo
     * @param password
     * @return
     */
    @RequestMapping("/enter")
    public Result enterRoom(@RequestParam(required = true) String roomNo,
                            String password) {
        User user = userService.getCurrentUser();
        roomService.userEnterChatRoom(user,roomNo,password);
        return Result.success().msg("进入聊天室成功!");
    }

    /**
     * 退出聊天室
     * @param roomNo
     * @return
     */
    @RequestMapping("/quit")
    public Result quitRoom(@RequestParam(required = true) String roomNo) {
        User user = userService.getCurrentUser();
        roomService.userQuitChatRoom(user,roomNo);
        return Result.success().msg("退出聊天室成功!");
    }

    /**
     * 聊天室在线用户
     * @param roomNo
     * @return
     */
    @RequestMapping("/user/online")
    public Result roomUserOnline(@RequestParam(required = true) String roomNo) {
        Set<UserChatRoomVO> userChatRoomVOSet =   roomService.getOnlineUser(roomNo);
        return Result.success().data(userChatRoomVOSet);
    }


}
