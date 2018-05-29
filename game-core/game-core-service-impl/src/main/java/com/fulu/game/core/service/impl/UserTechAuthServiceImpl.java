package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserTechAuthDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserTechAuthServiceImpl extends AbsCommonService<UserTechAuth, Integer> implements UserTechAuthService {

    @Autowired
    private UserTechAuthDao userTechAuthDao;
    @Autowired
    private TechTagService techTagService;
    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private UserTechInfoService userTechInfoService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<UserTechAuth, Integer> getDao() {
        return userTechAuthDao;
    }

    @Override
    public UserTechAuthVO save(UserTechAuthVO userTechAuthVO) {
        User user = userService.findById(userTechAuthVO.getUserId());
        Category category = categoryService.findById(userTechAuthVO.getCategoryId());
        userTechAuthVO.setStatus(TechAuthStatusEnum.AUTHENTICATION_ING.getType());
        userTechAuthVO.setMobile(user.getMobile());
        userTechAuthVO.setCategoryName(category.getName());
        userTechAuthVO.setUpdateTime(new Date());

        if (userTechAuthVO.getId() == null) {
            //查询是否有重复技能
            List<UserTechAuth> userTechAuths = findByCategoryAndUser(userTechAuthVO.getCategoryId(), userTechAuthVO.getUserId());
            if (userTechAuths.size() > 0) {
                throw new ServiceErrorException("不能添加重复的技能!");
            }
            userTechAuthVO.setCreateTime(new Date());
            userTechAuthDao.create(userTechAuthVO);
        } else {
            UserTechAuth oldUserTechAuth = findById(userTechAuthVO.getId());
            if (!oldUserTechAuth.getId().equals(userTechAuthVO.getId())) {
                //查询是否有重复技能
                List<UserTechAuth> userTechAuths = findByCategoryAndUser(userTechAuthVO.getCategoryId(), userTechAuthVO.getUserId());
                if (userTechAuths.size() > 0) {
                    throw new ServiceErrorException("不能添加重复的技能!");
                }
            }
            userTechAuthDao.update(userTechAuthVO);
        }
        //创建技能标签关联
        createTechTag(userTechAuthVO.getId(), userTechAuthVO.getTagIds());
        //创建游戏段位
        createTechDan(userTechAuthVO.getId(), userTechAuthVO.getCategoryId(), userTechAuthVO.getDanId());
        return userTechAuthVO;
    }

    @Override
    public List<UserTechAuth> findByStatusAndUserId(Integer userId, Integer status) {
        UserTechAuthVO param = new UserTechAuthVO();
        param.setUserId(userId);
        param.setStatus(status);
        return userTechAuthDao.findByParameter(param);
    }

    @Override
    public PageInfo<UserTechAuthVO> list(Integer pageNum, Integer pageSize, String orderBy, UserTechAuthVO requestVo) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "update_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<UserTechAuth> userTechAuths = userTechAuthDao.findByParameter(requestVo);
        List<UserTechAuthVO> userTechAuthVOList = new ArrayList<>();
        for (UserTechAuth userTechAuth : userTechAuths) {
            UserTechAuthVO userTechAuthVO = new UserTechAuthVO();
            BeanUtil.copyProperties(userTechAuth, userTechAuthVO);
            //用户段位信息
            UserTechInfo userTechInfo = findDanInfo(userTechAuthVO.getId());
            userTechAuthVO.setDanInfo(userTechInfo);
            //用户技能标签
            List<TechTag> techTagList = findTechTags(userTechAuthVO.getId());
            userTechAuthVO.setTagList(techTagList);
            userTechAuthVOList.add(userTechAuthVO);
        }
        PageInfo page = new PageInfo(userTechAuths);
        page.setList(userTechAuthVOList);
        return page;
    }


    public List<UserTechAuth> findByCategoryAndUser(Integer categoryId, Integer userId) {
        UserTechAuthVO param = new UserTechAuthVO();
        param.setCategoryId(categoryId);
        param.setUserId(userId);
        return userTechAuthDao.findByParameter(param);
    }


    @Override
    public UserTechAuthVO findTechAuthVOById(Integer id) {
        UserTechAuth userTechAuth = findById(id);
        if (userTechAuth == null) {
            return null;
        }
        Integer approveCount = userTechAuth.getApproveCount();
        Integer requireCount = approveCount < 5 ? Constant.DEFAULT_APPROVE_COUNT - approveCount : 0;
        UserTechAuthVO userTechAuthVO = new UserTechAuthVO();
        userTechAuthVO.setRequireCount(requireCount);
        BeanUtil.copyProperties(userTechAuth, userTechAuthVO);
        //查询用户所有技能标签
        List<TechTag> techTagList = findTechTags(userTechAuth.getId());
        userTechAuthVO.setTagList(techTagList);
        //查询技能的段位信息
        UserTechInfo danInfo = findDanInfo(userTechAuthVO.getId());
        userTechAuthVO.setDanInfo(danInfo);
        //查询该技能对应的游戏信息
        Category category = categoryService.findById(userTechAuthVO.getCategoryId());
        userTechAuthVO.setCategory(category);
        return userTechAuthVO;
    }


    /**
     * 查询用户段位信息
     *
     * @param techAuthId
     * @return
     */
    public UserTechInfo findDanInfo(Integer techAuthId) {
        List<UserTechInfo> userTechInfoList = userTechInfoService.findByTechAuthId(techAuthId);
        if (!userTechInfoList.isEmpty()) {
            return userTechInfoList.get(0);
        }
        return null;
    }

    /**
     * 通过用户Id查询用户技能认证信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserTechAuth> findByUserId(Integer userId) {
        UserTechAuthVO param = new UserTechAuthVO();
        param.setUserId(userId);
        List<UserTechAuth> userTechAuths = userTechAuthDao.findByParameter(param);
        return userTechAuths;
    }

    /**
     * 查询用户选择的所有技能标签
     *
     * @param techAuthId
     * @return
     */
    private List<TechTag> findTechTags(Integer techAuthId) {
        List<TechTag> techTagList = techTagService.findByTechAuthId(techAuthId);
        return techTagList;
    }


    /**
     * 创建用户技能标签
     *
     * @param techAuthId
     * @param tags
     */
    private void createTechTag(Integer techAuthId, Integer[] tags) {
        if (tags == null) {
            return;
        }
        techTagService.deleteByTechAuthId(techAuthId);
        for (Integer tagId : tags) {
            Tag tag = tagService.findById(tagId);
            TechTag techTag = new TechTag();
            techTag.setTagId(tagId);
            techTag.setName(tag.getName());
            techTag.setTechAuthId(techAuthId);
            techTag.setCreateTime(new Date());
            techTag.setUpdateTime(new Date());
            techTagService.create(techTag);
        }
    }

    /**
     * 创建用户游戏段位
     *
     * @param techAuthId
     * @param categoryId
     * @param dan
     */
    private void createTechDan(Integer techAuthId, Integer categoryId, Integer dan) {
        if (dan == null) {
            return;
        }
        userTechInfoService.deleteByTechAuthId(techAuthId);
        TechAttr techAttr = techAttrService.findByCategoryAndType(categoryId, TechAttrTypeEnum.DAN.getType());
        TechValue techValue = techValueService.findById(dan);
        UserTechInfo userTechInfo = new UserTechInfo();
        userTechInfo.setTechAttrId(techAttr.getId());
        userTechInfo.setAttr(techAttr.getName());
        userTechInfo.setTechAuthId(techAuthId);
        userTechInfo.setTechValueId(techValue.getId());
        userTechInfo.setValue(techValue.getName());
        userTechInfo.setRank(techValue.getRank());
        userTechInfo.setStatus(true);
        userTechInfo.setCreateTime(new Date());
        userTechInfo.setUpdateTime(new Date());
        userTechInfoService.create(userTechInfo);
    }


}
