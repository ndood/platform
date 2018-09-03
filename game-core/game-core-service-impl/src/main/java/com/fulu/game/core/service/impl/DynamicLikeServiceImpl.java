package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.DynamicLikeVO;
import com.fulu.game.core.search.component.DynamicSearchComponent;
import com.fulu.game.core.service.DynamicService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.DynamicLikeDao;
import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.service.DynamicLikeService;

import java.util.Date;
import java.util.List;


@Service("dynamicLikeService")
public class DynamicLikeServiceImpl extends AbsCommonService<DynamicLike,Long> implements DynamicLikeService {

    @Autowired
	private DynamicLikeDao dynamicLikeDao;


    @Autowired
    private UserService userService;

    @Autowired
    private DynamicService dynamicService;


    @Override
    public ICommonDao<DynamicLike, Long> getDao() {
        return dynamicLikeDao;
    }

    /**
     * 点赞接口
     *
     * @param dynamicLikeVO
     */
    @Override
    public void save(DynamicLikeVO dynamicLikeVO) {
        if(dynamicLikeVO == null){
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        Integer status = dynamicLikeVO.getStatus();
        if(status != null && status == 0 ){

        }
        dynamicLikeVO.setStatus(null);
        User user = userService.getCurrentUser();
        dynamicLikeVO.setFromUserId(user.getId().longValue());
        List<DynamicLike> list = dynamicLikeDao.findByParameter(dynamicLikeVO);
        dynamicLikeVO.setStatus(status);
        dynamicLikeVO.setCreateTime(new Date());
        dynamicLikeVO.setFromUserHeadUrl(user.getHeadPortraitsUrl());
        dynamicLikeVO.setFromUserNickname(user.getNickname());

        if(list != null && !list.isEmpty()){
            dynamicLikeDao.update(dynamicLikeVO);
        } else {
            create(dynamicLikeVO);
        }
        // TODO shijiaoyun 修改动态信息的点赞次数（以及ES中存是否已点赞信息：likeUserId）
        if(status != null && status == 0){//取消点赞
            dynamicService.updateIndexFilesById(dynamicLikeVO.getId(), false, -1, 0,false);
        } else {//点赞
            dynamicService.updateIndexFilesById(dynamicLikeVO.getId(), false, 1, 0,false);
        }
    }

    /**
     * 获取点赞记录列表接口
     *
     * @param pageNum
     * @param pageSize
     * @param dynamicId
     * @return
     */
    @Override
    public PageInfo<DynamicLike> list(Integer pageNum, Integer pageSize, Long dynamicId) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        DynamicLikeVO dynamicLikeVO = new DynamicLikeVO();
        dynamicLikeVO.setDynamicId(dynamicId);
        dynamicLikeVO.setStatus(1);
        List<DynamicLike> list = dynamicLikeDao.findByParameter(dynamicLikeVO);
        return new PageInfo<>(list);
    }
}
