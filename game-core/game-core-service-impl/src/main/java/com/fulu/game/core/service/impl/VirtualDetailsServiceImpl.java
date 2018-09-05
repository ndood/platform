package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.VirtualDetailsTypeEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualDetailsDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualDetailsService;
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
    private UserService userService;
    @Qualifier("userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    @Override
    public ICommonDao<VirtualDetails, Integer> getDao() {
        return virtualDetailsDao;
    }

    @Override
    public VirtualDetails createVirtualDetails(Integer userId,
                                               Integer virtualProductId,
                                               Integer price,
                                               VirtualDetailsTypeEnum virtualDetailsTypeEnum,
                                               VirtualProductTypeEnum virtualProductTypeEnum) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        return createVirtualDetails(user,
                virtualProductId,
                price,
                virtualDetailsTypeEnum,
                virtualProductTypeEnum);
    }

    @Override
    public VirtualDetails createVirtualDetails(User user,
                                               Integer virtualProductId,
                                               Integer price,
                                               VirtualDetailsTypeEnum virtualDetailsTypeEnum,
                                               VirtualProductTypeEnum virtualProductTypeEnum) {
        VirtualDetails details = new VirtualDetails();
        details.setUserId(user.getId());
        details.setMoney(price);
        details.setType(virtualDetailsTypeEnum.getType());
        Integer sum = 0;
        if (virtualDetailsTypeEnum.equals(VirtualDetailsTypeEnum.VIRTUAL_MONEY)) {
            sum = user.getVirtualBalance();
        } else if (virtualDetailsTypeEnum.equals(VirtualDetailsTypeEnum.CHARM)) {
            sum = user.getCharm() == null ? 0 : user.getCharm();
        }
        details.setSum(sum);
        details.setRemark(virtualProductTypeEnum.getMsg() + "id：" + virtualProductId);
        details.setCreateTime(DateUtil.date());
        create(details);
        return details;
    }
}
