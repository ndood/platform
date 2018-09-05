package com.fulu.game.core.service;

import com.fulu.game.common.enums.VirtualDetailsTypeEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualDetails;


/**
 * 虚拟币和魅力值详情流水表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 15:26:37
 */
public interface VirtualDetailsService extends ICommonService<VirtualDetails, Integer> {

    VirtualDetails createVirtualDetails(Integer fromUserId,
                                        Integer virtualProductId,
                                        Integer price,
                                        VirtualDetailsTypeEnum virtualDetailsTypeEnum,
                                        VirtualProductTypeEnum virtualProductTypeEnum);

    VirtualDetails createVirtualDetails(User user,
                                        Integer virtualProductId,
                                        Integer price,
                                        VirtualDetailsTypeEnum virtualDetailsTypeEnum,
                                        VirtualProductTypeEnum virtualProductTypeEnum);
}
