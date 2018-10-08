package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.RoomCategoryService;
import com.fulu.game.core.service.RoomService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        collectRoomCategory.setIcon("");
        collectRoomCategory.setIsActivate(true);
        collectRoomCategory.setSort(999);

        RoomCategory hotRoomCategory = new RoomCategory();
        hotRoomCategory.setId(998);
        hotRoomCategory.setName("热门");
        hotRoomCategory.setIcon("");
        hotRoomCategory.setIsActivate(true);
        hotRoomCategory.setSort(998);
        list.add(collectRoomCategory);
        list.add(hotRoomCategory);
        list.sort((RoomCategory c1, RoomCategory c2) -> c2.getSort().compareTo(c1.getSort()));
        return Result.success().data(list);
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


            return Result.success();
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
     *
     * @return
     */
    @RequestMapping("/mine")
    public Result roomCategoryMine() {
        User user = userService.getCurrentUser();
        RoomVO roomVO = roomService.findByOwner(user.getId());
        return Result.success().data(roomVO);
    }


}
