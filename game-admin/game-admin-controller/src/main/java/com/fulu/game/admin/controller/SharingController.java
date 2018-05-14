package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.SharingService;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/sharing")
public class SharingController {

    @Autowired
    private SharingService sharingService;
    @Autowired
    private AdminService adminService;

    /**
     * 新增文案（要判断是否重复）
     *
     * @param sharingVO
     * @return
     */
    @PostMapping("/save")
    public Result save(@ModelAttribute SharingVO sharingVO) {
        if (sharingVO.getGender() == 0) {
            return Result.error().msg("参数gender不能为0");
        }
        //同类型文案查重
        SharingVO reqSharingVO = new SharingVO();
        reqSharingVO.setGender(sharingVO.getGender());
        reqSharingVO.setShareType(sharingVO.getShareType());
        reqSharingVO.setStatus(true);
        List<Sharing> list = sharingService.findByParam(reqSharingVO);
        if (!CollectionUtil.isEmpty(list)) {
            return Result.error().msg("该类型文案已配置！");
        }
        Sharing sharing = new Sharing();
        BeanUtil.copyProperties(sharingVO, sharing);
        sharing.setStatus(true);//默认为启用
        sharing.setCreateTime(new Date());
        sharing.setUpdateTime(new Date());
        sharingService.create(sharing);
        Admin admin = adminService.getCurrentUser();
        log.info("文案操作==save== userId={},time={},id=", admin.getId(), sharing.getCreateTime(), sharing.getId());
        return Result.success().data(sharing).msg("保存成功！");
    }

    /**
     * 修改文案
     *
     * @param id
     * @param status
     * @param gender
     * @param shareType
     * @param content
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestParam("id") Integer id,
                         @RequestParam("status") Boolean status,
                         @RequestParam("gender") Integer gender,
                         @RequestParam("shareType") Integer shareType,
                         @RequestParam("content") String content
    ) {
        Sharing sharing = sharingService.findById(id);
        if (null == sharing) {
            return Result.error().msg("该记录不存在！");
        }
        if (gender == 0) {
            return Result.error().msg("参数gender不能为0");
        }
        sharing.setStatus(status);
        sharing.setGender(gender);
        sharing.setContent(content);
        sharing.setShareType(shareType);
        sharing.setUpdateTime(new Date());
        sharingService.update(sharing);
        Admin admin = adminService.getCurrentUser();
        log.info("文案操作==update== userId={},time={},id=", admin.getId(), sharing.getUpdateTime(), id);
        return Result.success().data(sharing).msg("修改成功！");
    }

    /**
     * 删除文案
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result delete(@RequestParam("id") Integer id) {
        sharingService.deleteById(id);
        Admin admin = adminService.getCurrentUser();
        log.info("文案操作==delete== userId={},time={},id=", admin.getId(), new Date(), id);
        return Result.success().msg("删除成功！");
    }

    /**
     * 文案列表
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    @PostMapping("/list")
    public Result list(@RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNum") Integer pageNum) {
        PageInfo<Sharing> shareList = sharingService.list(pageNum, pageSize);
        return Result.success().data(shareList).msg("查询列表成功！");
    }
}
