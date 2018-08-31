package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.VirtualDetailsTypeEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualDetailsDao;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualDetailsService;
import com.fulu.game.core.service.VirtualProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VirtualDetailsServiceImpl extends AbsCommonService<VirtualDetails, Integer> implements VirtualDetailsService {

    @Autowired
    private VirtualDetailsDao virtualDetailsDao;
    @Autowired
    private VirtualProductService virtualProductService;
    @Autowired
    private UserService userService;


    @Override
    public ICommonDao<VirtualDetails, Integer> getDao() {
        return virtualDetailsDao;
    }

    @Override
    public boolean createVirtualDetails(Integer userId, Integer virtualProductId, VirtualProductTypeEnum virtualProductTypeEnum) {
        Integer price = virtualProductService.findPriceById(virtualProductId);

        VirtualDetails details = new VirtualDetails();
        details.setUserId(userId);
        details.setSum(userService.findById(userId).getVirtualBalance());
        details.setMoney(Math.negateExact(price));
        details.setType(VirtualDetailsTypeEnum.VIRTUAL_MONEY.getType());
        details.setRemark(virtualProductTypeEnum.getMsg() + "；礼物id：" + virtualProductId);
        details.setCreateTime(DateUtil.date());
        create(details);
        return true;
    }
}
