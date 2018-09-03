package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualProduct;
import com.github.pagehelper.PageInfo;

import java.util.List;
import com.fulu.game.core.entity.vo.VirtualProductVO;


/**
 * 虚拟商品表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 10:01:57
 */
public interface VirtualProductService extends ICommonService<VirtualProduct, Integer> {
    
    List<VirtualProductVO> searchByvirtualProductVo(VirtualProductVO vpo);

    List<VirtualProduct> findAllGift();

    Integer findPriceById(Integer id);

    PageInfo<VirtualProduct> findGiftByPage(Integer pageNum, Integer pageSize);

    /**
     * 解锁虚拟商品
     *
     * @return
     */
    void unlockProduct(Integer userId , Integer virtualProductId);


    /**
     * 创建虚拟商品
     *
     * @return
     */
    VirtualProduct createVirtualProduct(VirtualProduct vp, Integer userId, String[] urls);
}
