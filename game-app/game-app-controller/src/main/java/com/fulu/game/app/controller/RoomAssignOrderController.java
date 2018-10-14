package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RoomEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.RoomAssignOrderVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.RoomAssignOrderService;
import com.fulu.game.core.service.RoomService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/room/assign")
public class RoomAssignOrderController extends BaseController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RoomAssignOrderService roomAssignOrderService;
    @Autowired
    private UserService userService;


    @PostMapping("/category")
    public Result assignCategory(@RequestParam(required = true) String roomNo) {
        Room room = roomService.findByRoomNo(roomNo);
        if(!RoomEnum.RoomTemplateEnum.ORDER.getType().equals(room.getTemplate())){
            return Result.error().msg("只有派单房才能派单!");
        }
        if(room.getCategoryId()==null){
            return Result.error().msg("该房间没有设置派单类型!");
        }
        Category category = categoryService.findById(room.getCategoryId());
        List<Category> categoryList =  categoryService.findThreeLevelCategory(category.getId());
        return Result.success().data(categoryList);
    }



    /**
     * 聊天室派单接口
     * @return
     */
    @PostMapping("/order")
    public Result assignOrder(@RequestParam(required = true) String roomNo,
                              @RequestParam(required = true) Integer categoryId,
                              @RequestParam(required = true) String content){
        User user =  userService.getCurrentUser();
        //创建派单消息
        roomAssignOrderService.create(roomNo,user.getId(),categoryId,content);
        return Result.success().msg("发送派单消息成功!");
    }


    /**
     * 完成聊天室派单
     * @param roomNo
     * @return
     */
    @PostMapping("/order/finish")
    public Result finishAssignOrder(@RequestParam(required = true) String roomNo){
        User user =  userService.getCurrentUser();
        //创建派单消息
        roomAssignOrderService.finishRoomAssignOrder(roomNo,user.getId());
        return Result.success().msg("派单消息完成!");
    }



    /**
     * 未完成聊天室派单列表
     * @param roomNo
     * @return
     */
    @PostMapping("/order/unfinished/list")
    public Result unfinishedAssignOrder(@RequestParam(required = true) String roomNo){
        User user =  userService.getCurrentUser();
        //创建派单消息
        List<RoomAssignOrderVO> roomAssignOrderVOS = roomAssignOrderService.unfinishedRoomAssignOrder(roomNo,user.getId());
        return Result.success().data(roomAssignOrderVOS);
    }




}
