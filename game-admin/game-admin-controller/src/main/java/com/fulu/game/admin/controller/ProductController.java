package com.fulu.game.admin.controller;

import com.fulu.game.admin.service.impl.AdminUserTechAuthServiceImpl;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.ProductTop;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.ProductTopVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.ProductTopService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class ProductController extends BaseController {

    @Autowired
    private ProductTopService productTopService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Qualifier(value = "adminUserTechAuthServiceImpl")
    @Autowired
    private AdminUserTechAuthServiceImpl userTechAuthService;


    @PostMapping(value = "/top/list")
    public Result topList(Integer pageNum,
                          Integer pageSize,
                          String  nickname,
                          String  mobile,
                          Integer categoryId) {
        PageInfo<ProductTop> pageInfo = productTopService.productList(pageNum,pageSize,nickname,mobile,categoryId);
        return Result.success().data(pageInfo);
    }


    @PostMapping(value = "/top/save")
    public Result save(@Valid ProductTopVO productTopVO) {
        Admin admin = adminService.getCurrentUser();
        User user = userService.findByMobile(productTopVO.getMobile());
        if (user == null) {
            return Result.error().msg("手机号错误!");
        }
        ProductTop  productTop = productTopService.findByUserAndCategory(user.getId(),productTopVO.getCategoryId());
        if(productTop!=null&&!productTop.getId().equals(productTopVO.getId())){
            return Result.error().msg("不能为同一个用户置顶同一个分类!");
        }
        log.info("管理员保存置顶:productTopVO:{};adminId:{};adminName:{};",productTopVO,admin.getId(),admin.getName());
        productTopVO.setAdminId(admin.getId());
        productTopVO.setAdminName(admin.getName());
        productTopVO.setStatus(false);
        productTopVO.setUserId(user.getId());
        if (productTopVO.getId() == null) {
            productTopVO.setCreateTime(new Date());
            productTopVO.setUpdateTime(new Date());
            productTopService.create(productTopVO);
        } else {
            productTopVO.setUpdateTime(new Date());
            productTopService.update(productTopVO);
        }
        return Result.success().msg("保存成功!");
    }

    /**
     * 查询用户所有可以排序的类型
     *
     * @param mobile
     * @return
     */
    @PostMapping(value = "/top/category")
    public Result category(String mobile) {
        User user = userService.findByMobile(mobile);
        if (user == null) {
            return Result.error().msg("手机号输入错误,该用户不存在!");
        }
        List<UserTechAuth> userTechAuths = userTechAuthService.findUserNormalTechs(user.getId());
        Map<Integer, String> data = new HashMap<>();
        userTechAuths.forEach((tech) -> {
            data.put(tech.getCategoryId(), tech.getCategoryName());
        });
        return Result.success().data(data);
    }

    /**
     * 商品指定上下架
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping(value = "/top/putaway")
    public Result putAway(Integer id, Boolean status) {
        String msg;
        if (status) {
            msg = "置顶上架成功!";
        } else {
            msg = "置顶下架成功!";
        }
        productTopService.productTopPutAway(id, status);
        return Result.success().msg(msg);
    }


}
