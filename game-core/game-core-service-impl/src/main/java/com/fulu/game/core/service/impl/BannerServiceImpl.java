package com.fulu.game.core.service.impl;

import com.fulu.game.common.exception.CommonException;
import com.fulu.game.common.utils.TimeUtil;
import com.fulu.game.core.dao.BannerDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.BannerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BannerServiceImpl extends AbsCommonService<Banner, Integer> implements BannerService {

    @Autowired
    private AdminService adminService;
    @Autowired
    private BannerDao bannerDao;

    @Override
    public ICommonDao<Banner, Integer> getDao() {
        return bannerDao;
    }

    @Override
    public Banner save(BannerVO bannerVO) {
        Admin admin = adminService.getCurrentUser();
        Banner banner = new Banner();
        BeanUtil.copyProperties(bannerVO, banner);
        banner.setOperatorId(admin.getId());
        banner.setOperatorName(admin.getName());
        banner.setDisable(true);
        banner.setCreateTime(new Date());
        banner.setUpdateTime(banner.getCreateTime());
        bannerDao.create(banner);
        log.info("{} 添加banner 操作，时间:{} ", admin.getName(), TimeUtil.defaultFormat(banner.getCreateTime()));
        return banner;
    }

    @Override
    public Banner update(BannerVO bannerVO) {
        Admin admin = adminService.getCurrentUser();
        Banner banner = bannerDao.findById(bannerVO.getId());
        if (null == banner) {
            throw new CommonException(CommonException.ExceptionCode.RECORD_NOT_EXSISTS);
        }
        banner.setPicUrl(bannerVO.getPicUrl());
        banner.setRedirectUrl(bannerVO.getRedirectUrl());
        banner.setRedirectType(bannerVO.getRedirectType());
        banner.setSort(bannerVO.getSort());
        banner.setOperatorId(admin.getId());
        banner.setOperatorName(admin.getName());
        banner.setUpdateTime(new Date());
        bannerDao.update(banner);
        log.info("{} 修改banner 操作，时间:{} ", admin.getName(), TimeUtil.defaultFormat(banner.getUpdateTime()));
        return banner;
    }

    @Override
    public void delete(Integer id) {
        Admin admin = adminService.getCurrentUser();
        bannerDao.deleteById(id);
        log.info("{} 删除banner 操作，时间:{},bannerId= {} ", admin.getName(), TimeUtil.defaultFormat(new Date()), id);
    }

    @Override
    public Banner disable(Integer id,Boolean disable){
        Admin admin = adminService.getCurrentUser();
        Banner banner = bannerDao.findById(id);
        banner.setDisable(disable);
        banner.setUpdateTime(new Date());
        bannerDao.update(banner);
        String operTpe = disable?"启用":"禁用";
        log.info("{} {}banner 操作，时间:{},bannerId= {} ", admin.getName(), operTpe,TimeUtil.defaultFormat(banner.getUpdateTime()), id);
        return banner;
    }

    @Override
    public PageInfo<Banner> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "sort DESC");
        List<Banner> list = bannerDao.findByParameter(null);
        return new PageInfo(list);
    }

    @Override
    public List<Banner> findByParam(BannerVO bannerVO){
        PageHelper.startPage(1, 1000, "sort DESC");
        return bannerDao.findByParameter(bannerVO);
    }

}
