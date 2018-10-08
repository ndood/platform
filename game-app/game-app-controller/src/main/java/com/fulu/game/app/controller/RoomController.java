package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.service.RoomCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/room")
public class RoomController extends BaseController {

    @Autowired
    private RoomCategoryService roomCategoryService;


    /**
     * 查找可用的房间分类
     * @return
     */
    @RequestMapping("/category")
    public Result roomCategory(){
        List<RoomCategory> list = roomCategoryService.findActivateRoomCategory();
        return Result.success().data(list);
    }


}
