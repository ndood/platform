package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualProductAttach;
import com.fulu.game.core.entity.vo.VirtualProductAttachVO;

import java.util.List;


/**
 * 虚拟商品附件表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 15:05:30
 */
public interface VirtualProductAttachService extends ICommonService<VirtualProductAttach, Integer> {

    List<VirtualProductAttach> findByParameter(VirtualProductAttachVO virtualProductAttachVO);


    List<VirtualProductAttach> findByOrderProIdUserId(Integer userId , Integer productId);

}
