package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.SourceIdEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.PriceFactorDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.PriceFactor;
import com.fulu.game.core.entity.vo.PriceFactorVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.PriceFactorService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PriceFactorServiceImpl extends AbsCommonService<PriceFactor, Integer> implements PriceFactorService {

    @Autowired
    private PriceFactorDao priceFactorDao;
    @Autowired
    private CategoryService categoryService;

    @Override
    public ICommonDao<PriceFactor, Integer> getDao() {
        return priceFactorDao;
    }

    @Override
    public PriceFactor findByNewPriceFactor() {
        PageHelper.startPage(1, 1, "id desc");
        PriceFactorVO priceFactorVO = new PriceFactorVO();
        priceFactorVO.setSourceId(SourceIdEnum.PILOT.getType());
        List<PriceFactor> list = priceFactorDao.findByParameter(priceFactorVO);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取CJ的价格系数
     *
     * @return 价格系数Bean
     */
    @Override
    public List<PriceFactorVO> findCjPriceFactor() {
        PriceFactorVO priceFactorVO = new PriceFactorVO();
        priceFactorVO.setSourceId(SourceIdEnum.CHINA_JOY.getType());
        List<PriceFactor> list = priceFactorDao.findByParameter(priceFactorVO);
        if (list.isEmpty()) {
            return null;
        }

        PriceFactor priceFactor = list.get(0);
        String categoryIds = priceFactor.getCategoryIds();
        List<PriceFactorVO> factorVOList = new ArrayList<>();
        if (!categoryIds.contains(Constant.DEFAULT_SPLIT_SEPARATOR)) {
            Category category = categoryService.findById(Integer.parseInt(categoryIds));
            if (category == null) {
                throw new ServiceErrorException("无法查询到Category分类");
            }
            PriceFactorVO vo = new PriceFactorVO();
            vo.setId(Integer.parseInt(categoryIds));
            vo.setName(category.getName());
            factorVOList.add(vo);
            return factorVOList;
        }

        String[] categoryIdArr = categoryIds.split(Constant.DEFAULT_SPLIT_SEPARATOR);
        for (String meta : categoryIdArr) {
            Category category = categoryService.findById(Integer.parseInt(meta));
            if (category == null) {
                throw new ServiceErrorException("无法查询到Category分类");
            }
            PriceFactorVO vo = new PriceFactorVO();
            vo.setId(Integer.parseInt(meta));
            vo.setName(category.getName());
            factorVOList.add(vo);
        }
        return factorVOList;
    }
}
