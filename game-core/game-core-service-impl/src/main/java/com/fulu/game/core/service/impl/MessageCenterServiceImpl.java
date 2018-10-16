package com.fulu.game.core.service.impl;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.service.AssignOrderSettingService;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.ImService;
import com.fulu.game.core.service.MessageCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MessageCenterServiceImpl implements MessageCenterService {

    @Autowired
    public ImService imService;
    @Autowired
    public AssignOrderSettingService assignOrderSettingService;
    @Autowired
    public CategoryService categoryService;

    public enum AdminImEnum{
        ASSIGN_ADMIN //派单IM
    }



    public void sendAssignOrderMsg(Integer categoryId, String roomNo, String content,Boolean status, String[] targetImIds) {
//        Set<Integer> userIdList = assignOrderSettingService.findOpenAssignUserByCategoryId(categoryId);
        Map<String,Object> extMap = new HashMap<>();
        Category category = categoryService.findById(categoryId);
        extMap.put("categoryName",category.getName());
        extMap.put("categoryIcon",category.getIcon());
        extMap.put("roomNo",roomNo);
        extMap.put("content",content);
        extMap.put("status",status);
        log.info("消息中心发送派单消息:{}",extMap);
        imService.sendMsgToImUser(targetImIds,AdminImEnum.ASSIGN_ADMIN.name(),"CREATE",extMap);
    }


}
