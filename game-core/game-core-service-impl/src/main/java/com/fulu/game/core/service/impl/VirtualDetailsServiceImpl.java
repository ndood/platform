package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.VirtualDetailsTypeEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualDetailsDao;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualDetailsService;
import com.fulu.game.core.service.VirtualProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class VirtualDetailsServiceImpl extends AbsCommonService<VirtualDetails, Integer> implements VirtualDetailsService {

    @Autowired
    private VirtualDetailsDao virtualDetailsDao;
    @Autowired
    private VirtualProductService virtualProductService;
    @Autowired
    private UserService userService;
    @Qualifier("userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    @Override
    public ICommonDao<VirtualDetails, Integer> getDao() {
        return virtualDetailsDao;
    }

    @Override
    public boolean createVirtualDetails(Integer userId,
                                        Integer virtualProductId,
                                        Integer price,
                                        VirtualDetailsTypeEnum virtualDetailsTypeEnum,
                                        VirtualProductTypeEnum virtualProductTypeEnum) {
        VirtualDetails details = new VirtualDetails();
        details.setUserId(userId);
        details.setMoney(price);
        details.setType(virtualDetailsTypeEnum.getType());
        Integer sum = 0;
        if (virtualDetailsTypeEnum.equals(VirtualDetailsTypeEnum.VIRTUAL_MONEY)) {
            sum = userService.findById(userId).getVirtualBalance();
        } else if (virtualDetailsTypeEnum.equals(VirtualDetailsTypeEnum.CHARM)) {
            UserInfoAuth auth = userInfoAuthService.findByUserId(userId);
            if (auth == null) {
                log.error("陪玩师不存在，userId:{}", userId);
                throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
            }
            Integer charm = auth.getCharm();
            if (charm != null) {
                sum = charm + price;
            }
        }
        details.setSum(sum);
        details.setRemark(virtualProductTypeEnum.getMsg() + "；礼物id：" + virtualProductId);
        details.setCreateTime(DateUtil.date());
        create(details);
        return true;
    }
}
