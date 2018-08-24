package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.FenqileOrderDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.FenqileOrder;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.FenqileOrderVO;
import com.fulu.game.core.entity.vo.searchVO.FenqileOrderSearchVO;
import com.fulu.game.core.service.FenqileOrderService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class FenqileOrderServiceImpl extends AbsCommonService<FenqileOrder, Integer> implements FenqileOrderService {

    private final FenqileOrderDao fenqileOrderDao;
    private final UserService userService;

    @Autowired
    public FenqileOrderServiceImpl(FenqileOrderDao fenqileOrderDao,
                                   UserService userService) {
        this.fenqileOrderDao = fenqileOrderDao;
        this.userService = userService;
    }

    @Override
    public ICommonDao<FenqileOrder, Integer> getDao() {
        return fenqileOrderDao;
    }

    @Override
    public PageInfo<FenqileOrderVO> list(Integer pageNum,
                                         Integer pageSize,
                                         String orderBy,
                                         FenqileOrderSearchVO searchVO) {

        if (StringUtils.isBlank(orderBy)) {
            orderBy = "tor.id DESC";
        }
        if (searchVO.getUserMobile() != null) {
            User user = userService.findByMobile(searchVO.getUserMobile());
            if (user == null) {
                throw new ServiceErrorException("用户手机号输入错误!");
            }
            searchVO.setUserId(user.getId());
        }
        if (searchVO.getServiceUserMobile() != null) {
            User user = userService.findByMobile(searchVO.getServiceUserMobile());
            if (user == null) {
                throw new ServiceErrorException("陪玩师手机号输入错误!");
            }
            searchVO.setServiceUserId(user.getId());
        }

        PageHelper.startPage(pageNum, pageSize, orderBy);

        Integer status = searchVO.getStatus();
        Integer[] statusList = OrderStatusGroupEnum.getByValue(status);
        if (!Arrays.isNullOrEmpty(statusList)) {
            searchVO.setStatusList(statusList);
        }

        List<FenqileOrderVO> fenqileOrderVOList = fenqileOrderDao.list(searchVO);
        if (CollectionUtils.isNotEmpty(fenqileOrderVOList)) {
            for (FenqileOrderVO meta : fenqileOrderVOList) {
                if (meta.getProductName().contains(" ")) {
                    meta.setProductName(meta.getProductName().split(" ")[0]);
                }
                meta.setStatusStr(OrderStatusEnum.getMsgByStatus(meta.getOrderStatus()));

                //设置应付金额
                Integer orderStatus = meta.getOrderStatus();
                meta.setPayableMoney(meta.getActualMoney());
                if (OrderStatusEnum.NON_PAYMENT.getStatus().equals(orderStatus)) {
                    meta.setActualMoney(null);
                }
            }
        }
        return new PageInfo<>(fenqileOrderVOList);
    }

    @Override
    public List<FenqileOrderVO> list(FenqileOrderSearchVO searchVO) {
        String orderBy = "tor.create_time desc";
        searchVO.setOrderBy(orderBy);

        Integer status = searchVO.getStatus();
        Integer[] statusList = OrderStatusGroupEnum.getByValue(status);
        if (!Arrays.isNullOrEmpty(statusList)) {
            searchVO.setStatusList(statusList);
        }

        List<FenqileOrderVO> fenqileOrderVOList = fenqileOrderDao.list(searchVO);
        if (CollectionUtils.isNotEmpty(fenqileOrderVOList)) {
            for (FenqileOrderVO meta : fenqileOrderVOList) {
                if (meta.getProductName().contains(" ")) {
                    meta.setProductName(meta.getProductName().split(" ")[0]);
                }
                meta.setStatusStr(OrderStatusEnum.getMsgByStatus(meta.getOrderStatus()));

                //设置应付金额
                Integer orderStatus = meta.getOrderStatus();
                meta.setPayableMoney(meta.getActualMoney());
                if (OrderStatusEnum.NON_PAYMENT.getStatus().equals(orderStatus)) {
                    meta.setActualMoney(null);
                }
            }
        }

        return fenqileOrderVOList;
    }

    @Override
    public FenqileOrderVO getTotalReconAmount(FenqileOrderSearchVO searchVO) {
        searchVO.setStatusList(OrderStatusGroupEnum.RECON_ALL.getStatusList());
        FenqileOrderVO resultVO = fenqileOrderDao.getTotalReconAmount(searchVO);
        resultVO.setUnReconTotalAmount(resultVO.getUnReconTotalAmount() == null ? new BigDecimal(0) : resultVO.getUnReconTotalAmount());
        resultVO.setUnReconCount(resultVO.getUnReconCount() == null ? 0 : resultVO.getUnReconCount());
        resultVO.setTotalAmount(resultVO.getTotalAmount() == null ? new BigDecimal(0) : resultVO.getTotalAmount());
        return resultVO;
    }
}
