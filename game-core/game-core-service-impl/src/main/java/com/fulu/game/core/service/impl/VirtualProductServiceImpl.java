package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.VirtualDetailsRemarkEnum;
import com.fulu.game.common.enums.VirtualDetailsTypeEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.VirtualProductException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductAttachDao;
import com.fulu.game.core.dao.VirtualProductDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.VirtualProductAttachVO;
import com.fulu.game.core.entity.vo.VirtualProductOrderVO;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualDetailsService;
import com.fulu.game.core.service.VirtualProductOrderService;
import com.fulu.game.core.service.VirtualProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private VirtualProductAttachDao virtualProductAttachDao;
    @Autowired
    private VirtualProductOrderService virtualProductOrderService;
    @Autowired
    private VirtualDetailsService virtualDetailsService;
    @Autowired
    private UserService userService;


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


        //先获取商品信息
        VirtualProduct vp = virtualProductDao.findById(virtualProductId);
        //获取附件信息
        VirtualProductAttachVO vpav = new VirtualProductAttachVO();
        vpav.setVirtualProductId(virtualProductId);
        List<VirtualProductAttach> vpaList = virtualProductAttachDao.findByParameter(vpav);

        //判断用户是否已经解锁过私照
        VirtualProductOrderVO vpo = new VirtualProductOrderVO();
        vpo.setTargetUserId(userId);
        vpo.setVirtualProductId(virtualProductId);
        List<VirtualProductOrder> vpList = virtualProductOrderService.findByParameter(vpo);

        if (vpList == null || vpList.size() == 0) {
            //判断用户钻石是否充足

            //先获取用户信息 和 购买信息
            User user = userService.findById(userId);
            int price = vp.getPrice().intValue();
            int vritualBalance = 0;
            if (user.getVirtualBalance() != null) {
                vritualBalance = user.getVirtualBalance().intValue();
            }

            if (price <= vritualBalance) {

                //扣除用户钻石
                user.setVirtualBalance(vritualBalance - price);
                userService.update(user);
                //生成购买订单
                VirtualProductOrder t = new VirtualProductOrder();
                t.setOrderNo(virtualProductOrderService.generateVirtualProductOrderNo());
                t.setVirtualProductId(vp.getId());
                t.setPrice(price);
                t.setVirtualProductId(virtualProductId);
                t.setFromUserId(vpaList.get(0).getUserId());
                t.setTargetUserId(userId);
                t.setCreateTime(new Date());

                virtualProductOrderService.create(t);
                
                //添加钻石流水记录
                VirtualDetails vd = new VirtualDetails();
                vd.setUserId(userId);
                vd.setSum(vritualBalance - price);
                vd.setMoney(price*-1);
                vd.setRelevantNo(t.getOrderNo());
                vd.setType(VirtualDetailsTypeEnum.VIRTUAL_MONEY.getType());
                if(vp.getType().intValue() == VirtualProductTypeEnum.VIRTUAL_GIFT.getType().intValue()){
                    vd.setRemark(VirtualDetailsRemarkEnum.GIFT_COST.getMsg());
                }else if(vp.getType().intValue() == VirtualProductTypeEnum.PERSONAL_PICS.getType().intValue()){
                    vd.setRemark(VirtualDetailsRemarkEnum.UNLOCK_PERSONAL_PICS.getMsg());
                }else if(vp.getType().intValue() == VirtualProductTypeEnum.IM_PROTECTED_PICS.getType().intValue()){
                    vd.setRemark(VirtualDetailsRemarkEnum.UNLOCK_PICS.getMsg());
                }else if(vp.getType().intValue() == VirtualProductTypeEnum.IM_PROTECTED_VOICE.getType().intValue()){
                    vd.setRemark(VirtualDetailsRemarkEnum.UNLOCK_VOICE.getMsg());
                }
                
                vd.setCreateTime(new Date());

                virtualDetailsService.create(vd);
            } else {

                //用户钻石不足
                throw new VirtualProductException(VirtualProductException.ExceptionCode.BALANCE_NOT_ENOUGH_EXCEPTION);
            }

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
            vpa.setUrl(urls[i]);
            vpa.setCreateTime(new Date());
            virtualProductAttachDao.create(vpa);
        }

        return vp;
    }
}
