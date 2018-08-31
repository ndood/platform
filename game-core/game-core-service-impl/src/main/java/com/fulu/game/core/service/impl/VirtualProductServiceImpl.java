package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductDao;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.fulu.game.core.service.VirtualProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class VirtualProductServiceImpl extends AbsCommonService<VirtualProduct, Integer> implements VirtualProductService {

    @Autowired
    private VirtualProductDao virtualProductDao;


    @Override
    public ICommonDao<VirtualProduct, Integer> getDao() {
        return virtualProductDao;
    }

    @Override
    public List<VirtualProduct> findAllGift() {
        return virtualProductDao.findAllGift();
    }

    @Override
    public PageInfo<VirtualProduct> findGiftByPage(Integer pageNum, Integer pageSize) {
        String orderBy = "sort ASC";
        PageHelper.startPage(pageNum, pageSize, orderBy);

        VirtualProductVO vo = new VirtualProductVO();
        vo.setDelFlag(Boolean.FALSE);
        List<VirtualProduct> productList = virtualProductDao.findByParameter(vo);
        return new PageInfo<>(productList);
    }

    @Override
    public Integer findPriceById(Integer id) {
        VirtualProduct virtualProduct = virtualProductDao.findById(id);
        if (virtualProduct == null) {
            log.error("虚拟商品id:{}不存在", id);
            throw new ServiceErrorException("虚拟商品不存在");
        }
        return virtualProduct.getPrice();
    }

    @Override
    public List<VirtualProductVO> searchByvirtualProductVo(VirtualProductVO vpo) {

        List<VirtualProductVO> list = null;

        try {

            list =  virtualProductDao.findByVirtualProductVo(vpo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
