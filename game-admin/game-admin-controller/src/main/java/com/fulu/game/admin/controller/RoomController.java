package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.RoomCategoryService;
import com.fulu.game.core.service.RoomService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
public class RoomController extends BaseController {


    @Autowired
    private RoomCategoryService roomCategoryService;

    @Autowired
    private RoomService roomService;

    @PostMapping(value = "/category/list")
    public Result category(){
        List<RoomCategory> roomCategoryList = roomCategoryService.list();
        return Result.success().data(roomCategoryList);
    }


    @PostMapping(value = "/save")
    public Result save(@Valid RoomVO roomVO){
        RoomVO result = roomService.save(roomVO);
        return Result.success().data(result);
    }


    @PostMapping(value = "/list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize,
                       String name){
        PageInfo<RoomVO> page = roomService.list(pageNum,pageSize,name);
        return Result.success().data(page);
    }



}
