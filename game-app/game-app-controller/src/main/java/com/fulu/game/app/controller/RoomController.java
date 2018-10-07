package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.RoomCategoryService;
import com.fulu.game.core.service.RoomService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 查找所有可用的房间分类
     * @return
     */
    @RequestMapping("/category")
    public Result roomCategoryAll(){
        List<RoomCategory> list = roomCategoryService.findActivateRoomCategory();


        return Result.success().data(list);
    }


    /**
     * 查询所有热门房间
     * @return
     */
    @RequestMapping("/hot")
    public Result roomCategoryHot(@RequestParam(required = true) Integer pageNum,
                                  @RequestParam(required = true) Integer pageSize){
        PageInfo<RoomVO> roomVOList = roomService.findUsableRoomsByHot(pageNum,pageSize);
        return Result.success().data(roomVOList);
    }



    /**
     * 查找普通房间
     * @return
     */
    @RequestMapping("/common")
    public Result roomCategoryCommon(@RequestParam(required = true) Integer roomCategoryId){
        PageInfo<RoomVO> roomVOList = roomService.findUsableRoomsByRoomCategory(roomCategoryId);
        return Result.success().data(roomVOList);
    }


    /**
     * 我的房间
     * @return
     */
    @RequestMapping("/mine")
    public Result roomCategoryMine(){
        User user = userService.getCurrentUser();
        RoomVO roomVO = roomService.findByOwner(user.getId());
        return Result.success().data(roomVO);
    }






}
