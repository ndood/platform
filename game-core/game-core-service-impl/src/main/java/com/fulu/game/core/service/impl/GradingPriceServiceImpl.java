package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.dao.GradingPriceDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.GradingPrice;
import com.fulu.game.core.entity.vo.GradingPriceVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.GradingPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class GradingPriceServiceImpl extends AbsCommonService<GradingPrice, Integer> implements GradingPriceService {

    @Autowired
    private GradingPriceDao gradingPriceDao;
    @Autowired
    private AdminService adminService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public ICommonDao<GradingPrice, Integer> getDao() {
        return gradingPriceDao;
    }


    @Override
    public GradingPrice create(Integer pid, String name, Integer rank, BigDecimal price) {
        GradingPrice parentGradingPrice = findById(pid);
        if (parentGradingPrice == null) {
            throw new ServiceErrorException("父类不能为空!");
        }
        Admin admin = adminService.getCurrentUser();
        GradingPrice gradingPrice = new GradingPrice();
        gradingPrice.setPid(pid);
        gradingPrice.setCategoryId(parentGradingPrice.getCategoryId());
        gradingPrice.setName(name);
        gradingPrice.setRank(rank);
        gradingPrice.setPrice(price);
        gradingPrice.setType(parentGradingPrice.getType());
        gradingPrice.setAdminId(admin.getId());
        gradingPrice.setAdminName(admin.getName());
        gradingPrice.setCreateTime(new Date());
        gradingPrice.setUpdateTime(new Date());
        create(gradingPrice);
        return gradingPrice;
    }


    @Override
    public GradingPrice update(Integer id, String name, Integer rank, BigDecimal price) {
        GradingPrice gradingPrice = findById(id);
        gradingPrice.setName(name);
        gradingPrice.setRank(rank);
        gradingPrice.setPrice(price);
        gradingPrice.setUpdateTime(new Date());
        update(gradingPrice);
        return gradingPrice;
    }


    public List<GradingPriceVO> findByCategoryAndType(int categoryId, int type) {
        GradingPriceVO param = new GradingPriceVO();
        param.setCategoryId(categoryId);
        param.setType(type);
        param.setPid(1);
        List<GradingPrice> gradingPriceList = gradingPriceDao.findByParameter(param);
        List<GradingPriceVO> parentList = CollectionUtil.copyNewCollections(findByPid(gradingPriceList.get(0).getId()), GradingPriceVO.class);
        for (GradingPriceVO gradingPriceVO : parentList) {
            List<GradingPrice> sonGradingPrices = findByPid(gradingPriceVO.getId());
            gradingPriceVO.setChildren(CollectionUtil.copyNewCollections(sonGradingPrices, GradingPriceVO.class));
        }
        return parentList;
    }


    @Override
    public List<GradingPriceVO> findVoByPid(int pid) {
        GradingPrice parentGradingPrice = findById(pid);
        if (parentGradingPrice == null) {
            throw new ServiceErrorException("父类不能为空!");
        }
        Category category = null;
        if (parentGradingPrice.getCategoryId() != null) {
            category = categoryService.findById(parentGradingPrice.getCategoryId());
        }
        GradingPriceVO param = new GradingPriceVO();
        param.setPid(pid);
        List<GradingPriceVO> list = CollectionUtil.copyNewCollections(gradingPriceDao.findByParameter(param), GradingPriceVO.class);
        for (GradingPriceVO vo : list) {
            if (pid > 1) {
                List<GradingPrice> childGradingPrices = findByPid(vo.getId());
                vo.setChildren(CollectionUtil.copyNewCollections(childGradingPrices, GradingPriceVO.class));
            }
            if (category != null) {
                vo.setCategoryName(category.getName());
            } else {
                Category selfCategory = categoryService.findById(vo.getCategoryId());
                vo.setCategoryName(selfCategory.getName());
            }
        }
        return list;
    }


    @Override
    public List<GradingPrice> findByPid(int pid) {
        GradingPriceVO param = new GradingPriceVO();
        param.setPid(pid);
        return gradingPriceDao.findByParameter(param);
    }


    @Override
    public BigDecimal findRangePrice(int categoryId, int startGradingId, int endGradingId) {
        GradingPrice startGradingPrice = findById(startGradingId);
        GradingPrice endGradingPrice = findById(endGradingId);
        if (startGradingPrice == null || endGradingPrice == null) {
            throw new ServiceErrorException("段位设置错误,请重新设置!");
        }
        if (!startGradingPrice.getCategoryId().equals(endGradingPrice.getCategoryId())
                || !startGradingPrice.getType().equals(endGradingPrice.getType())) {
            throw new ServiceErrorException("段位类型不匹配,请重新选择!");
        }
        Double result = gradingPriceDao.findRangePrice(categoryId, startGradingPrice.getRank(), endGradingPrice.getRank());
        return new BigDecimal(result.toString());
    }


    @Override
    public void updateParentGradingTime(int pid) {
        Admin admin = adminService.getCurrentUser();
        GradingPrice gradingPrice = findById(pid);
        if(gradingPrice.getId()<100){
            GradingPrice parentGradingPrice = new GradingPrice();
            parentGradingPrice.setId(gradingPrice.getId());
            parentGradingPrice.setUpdateTime(new Date());
            parentGradingPrice.setAdminName(admin.getName());
            parentGradingPrice.setAdminId(admin.getId());
            update(parentGradingPrice);
        }else{
            updateParentGradingTime(gradingPrice.getPid());
        }

    }


}
