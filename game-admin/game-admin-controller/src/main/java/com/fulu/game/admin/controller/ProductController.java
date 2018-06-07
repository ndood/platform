package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.ProductTopVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.ProductTopService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/product")
public class ProductController extends BaseController{

    @Autowired
    private ProductTopService productTopService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTechAuthService userTechAuthService;



    @PostMapping(value = "/top/save")
    public Result save(@Valid ProductTopVO productTopVO){
        Admin admin = adminService.getCurrentUser();
        productTopVO.setAdminId(admin.getId());
        productTopVO.setAdminName(admin.getName());
        productTopVO.setStatus(false);
        if(productTopVO.getId()==null){
            productTopVO.setCreateTime(new Date());
            productTopVO.setUpdateTime(new Date());
            productTopService.create(productTopVO);
        }else{
            productTopVO.setUpdateTime(new Date());
            productTopService.update(productTopVO);
        }
        return Result.success().msg("保存成功!");
    }

    /**
     * 查询用户所有可以排序的类型
     * @param mobile
     * @return
     */
    @PostMapping(value = "/top/category")
    public Result category(String mobile){
        User user = userService.findByMobile(mobile);
        if(user==null){
            return Result.error().msg("手机号输入错误,该用户不存在!");
        }
        List<UserTechAuth> userTechAuths = userTechAuthService.findUserNormalTechs(user.getId());
        Map<Integer,String> data = new HashMap<>();
        userTechAuths.forEach((tech)->{
            data.put(tech.getCategoryId(),tech.getCategoryName());
        });
        return Result.success().data(data);
    }

    /**
     * 商品指定上下架
     * @param id
     * @param status
     * @return
     */
    @PostMapping(value = "/top/putaway")
    public Result putAway(Integer id,Boolean status){
        String msg;
        if(status){
            msg = "商品上架成功!";
        }else{
            msg = "商品下架成功!";
        }
        productTopService.productTopPutAway(id,status);
        return Result.success().msg(msg);
    }


}
