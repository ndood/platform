package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.VirtualProductException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductDao;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.entity.VirtualProductAttach;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.fulu.game.core.service.VirtualProductAttachService;
import com.fulu.game.core.service.VirtualProductOrderService;
import com.fulu.game.core.service.VirtualProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class VirtualProductServiceImpl extends AbsCommonService<VirtualProduct, Integer> implements VirtualProductService {

    @Autowired
    private VirtualProductDao virtualProductDao;
    @Autowired
    private VirtualProductOrderService virtualProductOrderService;
    @Autowired
    private VirtualProductAttachService virtualProductAttachService;
    @Autowired
    private OssUtil ossUtil;


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
        vo.setType(VirtualProductTypeEnum.VIRTUAL_GIFT.getType());
        vo.setDelFlag(Boolean.FALSE);
        List<VirtualProduct> productList = virtualProductDao.findByParameter(vo);
        return new PageInfo<>(productList);
    }

    @Override
    public VirtualProduct add(VirtualProduct virtualProduct) {
        virtualProduct.setType(VirtualProductTypeEnum.VIRTUAL_GIFT.getType());
        virtualProduct.setObjectUrl(ossUtil.activateOssFile(virtualProduct.getObjectUrl()));
        virtualProduct.setCreateTime(DateUtil.date());
        virtualProduct.setUpdateTime(DateUtil.date());
        virtualProduct.setDelFlag(Boolean.FALSE);
        virtualProductDao.create(virtualProduct);
        return virtualProduct;
    }

    @Override
    public Integer findPriceById(Integer id) {
        VirtualProduct virtualProduct = virtualProductDao.findById(id);
        if (virtualProduct == null) {
            log.error("虚拟商品id:{}不存在", id);
            throw new VirtualProductException(VirtualProductException.ExceptionCode.NOT_EXIST);
        }
        return virtualProduct.getPrice();
    }

    @Override
    public List<VirtualProductVO> searchByvirtualProductVo(VirtualProductVO vpo) {

        List<VirtualProductVO> list = null;

        try {

            list = virtualProductDao.findByVirtualProductVo(vpo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    @Override
    @Transactional
    public void unlockProduct(Integer userId, Integer virtualProductId) {

        //获取附件信息
        List<VirtualProductAttach> vpaList = virtualProductAttachService.findByProductId(virtualProductId);

        //判断用户是否已经解锁过私照
        boolean isUnlock = virtualProductOrderService.isAlreadyUnlock(userId, virtualProductId);

        if (!isUnlock) {
            virtualProductOrderService.createVirtualOrder(userId, vpaList.get(0).getUserId(), virtualProductId);

        }
    }


    @Override
    @Transactional
    public VirtualProduct createVirtualProduct(VirtualProduct vp, Integer userId, String[] urls) {

        //添加商品信息
        virtualProductDao.create(vp);

        //添加商品附件信息
        for (int i = 0; i < urls.length; i++) {
            VirtualProductAttach vpa = new VirtualProductAttach();
            vpa.setUserId(userId);
            vpa.setVirtualProductId(vp.getId());
            vpa.setUrl(ossUtil.activateOssFile(urls[i]));
            vpa.setCreateTime(new Date());
            virtualProductAttachService.create(vpa);
        }

        return vp;
    }

    @Override
    public List<VirtualProductVO> findByVirtualProductVo(VirtualProductVO virtualProductVO) {
        return virtualProductDao.findByVirtualProductVo(virtualProductVO);
    }
}
