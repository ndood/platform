package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.github.pagehelper.PageInfo;

import java.util.List;


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

    /**
     * 分页获取礼物列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 分页礼物列表
     */
    PageInfo<VirtualProduct> findGiftByPage(Integer pageNum, Integer pageSize);

    /**
     * 新增礼物
     *
     * @param virtualProduct 礼物Bean
     * @return 礼物Bean
     */
    VirtualProduct add(VirtualProduct virtualProduct);

    /**
     * 解锁虚拟商品
     *
     * @return
     */
    void unlockProduct(Integer userId, Integer virtualProductId);
}
