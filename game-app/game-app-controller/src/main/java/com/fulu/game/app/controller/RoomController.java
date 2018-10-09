package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.entity.vo.RoomCategoryVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.RoomCategoryService;
import com.fulu.game.core.service.RoomService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        hotRoomCategory.setIcon( "https://game-play.oss-cn-hangzhou.aliyuncs.com/img/Chatroom_hot_default%403x.png");
        hotRoomCategory.setSelectedIcon("https://game-play.oss-cn-hangzhou.aliyuncs.com/img/Chatroom_hot_selected%403x.png");
        hotRoomCategory.setIsActivate(true);
        hotRoomCategory.setSort(998);

        if(!list.isEmpty()){
            list.add(collectRoomCategory);
            list.add(hotRoomCategory);
            list.sort((RoomCategory c1, RoomCategory c2) -> c2.getSort().compareTo(c1.getSort()));
        }
        User user = userService.getCurrentUser();
        List<RoomCategoryVO> result = roomCategoryService.getRoomListCategory(list,user.getId());
        return Result.success().data(result);
    }


    /**
     * 房间列表
     * @return
     */
    @RequestMapping("/list")
    public Result roomCategoryCommon(@RequestParam(required = true) Integer pageNum,
                                     @RequestParam(required = true) Integer pageSize,
                                     @RequestParam(required = true) Integer roomCategoryId) {
        if(Integer.valueOf(999).equals(roomCategoryId)){//收藏列表
            User user = userService.getCurrentUser();
            PageInfo<RoomVO> roomVOList = roomService.findCollectRoomByUser(pageNum,pageSize,user.getId());
            return Result.success().data(roomVOList);
        }else if(Integer.valueOf(998).equals(roomCategoryId)){//热门列表
            PageInfo<RoomVO> roomVOList = roomService.findUsableRoomsByHot(pageNum, pageSize);
            return Result.success().data(roomVOList);
        }else{
            PageInfo<RoomVO> roomVOList = roomService.findUsableRoomsByRoomCategory(pageNum,pageSize,roomCategoryId);
            return Result.success().data(roomVOList);
        }
    }


    /**
     * 我的房间
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
     * @return
     */
    @RequestMapping("/setting/update")
    public Result roomSettingSave(String roomNo,
                                  String icon,
                                  String name,
                                  Boolean isShow,
                                  Boolean isLock,
                                  String password,
                                  String notice,
                                  String slogan){
        User user = userService.getCurrentUser();




        return Result.success();
    }

}
