package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserTechAuthDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.UserTechAuthTO;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.entity.vo.TechAttrVO;
import com.fulu.game.core.entity.vo.TechValueVO;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.entity.vo.searchVO.UserTechAuthSearchVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.push.AdminPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
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
    private UserTechAuthRejectService userTechAuthRejectService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private AdminPushServiceImpl adminPushService;

    @Override
    public ICommonDao<UserTechAuth, Integer> getDao() {
        return userTechAuthDao;
    }



    @Override
    public UserTechAuthTO save(UserTechAuthTO userTechAuthTO) {
        log.info("修改认证技能:userTechAuthVO:{}",userTechAuthTO);
        User user = userService.getCurrentUser();
        Category category = categoryService.findById(userTechAuthTO.getCategoryId());
        userTechAuthTO.setStatus(TechAuthStatusEnum.AUTHENTICATION_ING.getType());
        userTechAuthTO.setMobile(user.getMobile());
        userTechAuthTO.setGradePicUrl(ossUtil.activateOssFile(userTechAuthTO.getGradePicUrl()));
        userTechAuthTO.setCategoryName(category.getName());
        userTechAuthTO.setUpdateTime(new Date());
        userTechAuthTO.setIsActivate(false);
        if (userTechAuthTO.getId() == null){
            //查询是否有重复技能
            List<UserTechAuth> userTechAuthes = findByCategoryAndUser(userTechAuthTO.getCategoryId(), userTechAuthTO.getUserId());
            if (userTechAuthes.size() > 0) {
                throw new ServiceErrorException("不能添加重复的技能!");
            }
            userTechAuthTO.setCreateTime(new Date());
            create(userTechAuthTO);
        }else {
            UserTechAuth oldUserTechAuth = findById(userTechAuthTO.getId());
            if(oldUserTechAuth.getStatus().equals(TechAuthStatusEnum.FREEZE.getType())){
                throw new  UserAuthException(UserAuthException.ExceptionCode.USER_TECH_FREEZE);
            }
            if (!oldUserTechAuth.getId().equals(userTechAuthTO.getId())) {
                //查询是否有重复技能
                List<UserTechAuth> userTechAuths = findByCategoryAndUser(userTechAuthTO.getCategoryId(), userTechAuthTO.getUserId());
                if (userTechAuths.size() > 0) {
                    throw new ServiceErrorException("不能添加重复的技能!");
                }
            }
            update(userTechAuthTO);
            if(!oldUserTechAuth.getGradePicUrl().equals(userTechAuthTO.getGradePicUrl())){
                ossUtil.deleteFile(oldUserTechAuth.getGradePicUrl());
            }
            //删除重新认证的商品
            productService.disabledProductByTech(userTechAuthTO.getId());

        }
        //创建技能标签关联
        saveTechTag(userTechAuthTO.getId(), userTechAuthTO.getTagIds());
        //创建游戏段位
        saveTechAttr(userTechAuthTO);
        return userTechAuthTO;
    }





    @Override
    public UserTechAuth reject(Integer id, String reason) {
        Admin admin = adminService.getCurrentUser();
        log.info("驳回技能认证信息:adminId:{};adminName:{};authInfoId:{},reason:{}",admin.getId(),admin.getName(),id,reason);
        UserTechAuth userTechAuth = findById(id);
        if(userTechAuth.getStatus().equals(TechAuthStatusEnum.FREEZE.getType())){
            throw new  UserAuthException(UserAuthException.ExceptionCode.USER_TECH_FREEZE);
        }
        //重置技能好友认证状态
        userTechAuth.setStatus(TechAuthStatusEnum.NO_AUTHENTICATION.getType());
        userTechAuth.setUpdateTime(new Date());
        update(userTechAuth);
        //添加拒绝原因
        UserTechAuthReject userTechAuthReject = new UserTechAuthReject();
        userTechAuthReject.setReason(reason);
        userTechAuthReject.setUserTechAuthId(userTechAuth.getStatus());
        userTechAuthReject.setUserId(userTechAuth.getUserId());
        userTechAuthReject.setUserTechAuthId(id);
        userTechAuthReject.setUserTechAuthStatus(userTechAuth.getStatus());
        userTechAuthReject.setAdminId(admin.getId());
        userTechAuthReject.setAdminName(admin.getName());
        userTechAuthReject.setCreateTime(new Date());
        userTechAuthRejectService.create(userTechAuthReject);
        //给用户推送通知
        adminPushService.techAuthAuditFail(userTechAuth.getUserId(),reason);

        //同步下架用户该技能商品
        productService.disabledProductByTech(userTechAuth.getId());

        return userTechAuth;
    }


    @Override
    public UserTechAuth pass(Integer id) {
        try {
            Admin admin = adminService.getCurrentUser();
            log.info("技能审核通过:管理员操作:adminId:{};adminName:{};authInfoId:{}",admin.getId(),admin.getName(),id);
        }catch (Exception e){
            log.info("技能审核通过:用户好友操作:authInfoId:{}",id);
        }
        UserTechAuth userTechAuth = findById(id);
        if(userTechAuth.getStatus().equals(TechAuthStatusEnum.FREEZE.getType())){
            throw new  UserAuthException(UserAuthException.ExceptionCode.USER_TECH_FREEZE);
        }
        userTechAuth.setStatus(TechAuthStatusEnum.NORMAL.getType());
        update(userTechAuth);
        //给用户推送通知
        adminPushService.techAuthAuditSuccess(userTechAuth.getUserId());

        //技能下商品置为正常
        productService.recoverProductActivateByTechAuthId(userTechAuth.getId());
        return userTechAuth;
    }

    @Override
    public UserTechAuth freeze(Integer id, String reason) {
        Admin admin = adminService.getCurrentUser();
        log.info("冻结技能认证信息:adminId:{};adminName:{};authInfoId:{},reason:{}",admin.getId(),admin.getName(),id,reason);
        //重置技能好友认证状态
        UserTechAuth userTechAuth = findById(id);
        //重置技能好友认证状态
        userTechAuth.setStatus(TechAuthStatusEnum.FREEZE.getType());
        userTechAuth.setUpdateTime(new Date());
        update(userTechAuth);
        //添加拒绝原因
        UserTechAuthReject userTechAuthReject = new UserTechAuthReject();
        userTechAuthReject.setReason(reason);
        userTechAuthReject.setUserTechAuthId(userTechAuth.getStatus());
        userTechAuthReject.setUserId(userTechAuth.getUserId());
        userTechAuthReject.setUserTechAuthStatus(userTechAuth.getStatus());
        userTechAuthReject.setUserTechAuthId(id);
        userTechAuthReject.setAdminId(admin.getId());
        userTechAuthReject.setAdminName(admin.getName());
        userTechAuthReject.setCreateTime(new Date());
        userTechAuthRejectService.create(userTechAuthReject);

        //同步下架用户该技能商品
        productService.disabledProductByTech(userTechAuth.getId());
        return userTechAuth;
    }

    @Override
    public UserTechAuth unFreeze(Integer id) {
        Admin admin = adminService.getCurrentUser();
        log.info("解冻技能认证信息:adminId:{};adminName:{};authInfoId:{}",admin.getId(),admin.getName(),id);
        UserTechAuth userTechAuth = findById(id);
        userTechAuth.setStatus(TechAuthStatusEnum.AUTHENTICATION_ING.getType());
        update(userTechAuth);

        //技能下商品置为正常
        productService.recoverProductActivateByTechAuthId(userTechAuth.getId());
        return userTechAuth;
    }

    @Override
    public List<UserTechAuth> findByStatusAndUserId(int userId, Integer status) {
        UserTechAuthVO param = new UserTechAuthVO();
        param.setUserId(userId);
        param.setStatus(status);
        return userTechAuthDao.findByParameter(param);
    }

    @Override
    public void updateByCategory(Category category) {
        if(category.getName()==null){
            return;
        }
        userTechAuthDao.updateByCategory(category);
    }

    @Override
    public PageInfo<UserTechAuthVO> list(Integer pageNum, Integer pageSize, UserTechAuthSearchVO userTechAuthSearchVO) {
        if (StringUtils.isBlank(userTechAuthSearchVO.getOrderBy())) {
            userTechAuthSearchVO.setOrderBy("update_time desc");
        }
        PageHelper.startPage(pageNum, pageSize, userTechAuthSearchVO.getOrderBy());
        List<UserTechAuth> userTechAuths = userTechAuthDao.search(userTechAuthSearchVO);
        List<UserTechAuthVO> userTechAuthVOList = new ArrayList<>();
        for (UserTechAuth userTechAuth : userTechAuths) {
            UserTechAuthVO userTechAuthVO = new UserTechAuthVO();
            BeanUtil.copyProperties(userTechAuth, userTechAuthVO);
            //查找用户基础信息
            User user = userService.findById(userTechAuthVO.getUserId());
            userTechAuthVO.setNickname(user.getNickname());
            userTechAuthVO.setGender(user.getGender());
            //查找用户技能标签
            List<TagVO> groupTags = findAllCategoryTagSelected(userTechAuth.getCategoryId(),userTechAuth.getId(),Boolean.TRUE);
            userTechAuthVO.setGroupTags(groupTags);
            //查找用户段位和大区
            List<TechAttrVO> groupAttrs = findAllCategoryAttrSelected(userTechAuth.getCategoryId(),userTechAuth.getId(),Boolean.TRUE);
            userTechAuthVO.setGroupAttrs(groupAttrs);
            userTechAuthVOList.add(userTechAuthVO);
            //查询是否存在开始接单
            UserAutoReceiveOrder autoReceiveOrder =   userAutoReceiveOrderService.findByTechId(userTechAuth.getId());
            if(autoReceiveOrder==null){
                userTechAuthVO.setAutoOrder(false);
            }else{
                userTechAuthVO.setAutoOrder(true);
            }
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
    public UserTechAuthVO findTechAuthVOById(Integer id,Integer categoryId) {
        UserTechAuth userTechAuth = findById(id);
        if (userTechAuth == null) {
            if(categoryId==null){
                throw new ServiceErrorException("没有选择游戏!");
            }
            userTechAuth = new UserTechAuth();
            userTechAuth.setCategoryId(categoryId);
        }
        UserTechAuthVO userTechAuthVO = new UserTechAuthVO();
        BeanUtil.copyProperties(userTechAuth, userTechAuthVO);
        if(userTechAuthVO.getId()!=null){
            //审核不通过原因
            UserTechAuthReject techAuthReject =userTechAuthRejectService.findLastRecordByTechAuth(userTechAuthVO.getId(),userTechAuthVO.getStatus());
            if(techAuthReject!=null){
                userTechAuthVO.setReason(techAuthReject.getReason());
            }
        }
        //查询用户所有技能标签
        List<TechTag> techTagList = findTechTags(userTechAuthVO.getId());
        userTechAuthVO.setTagList(techTagList);
        //查询技能的段位信息
        UserTechInfo danInfo = findDanInfo(userTechAuthVO.getId());
        userTechAuthVO.setDanInfo(danInfo);
        //查询该技能对应的游戏信息
        Category category = categoryService.findById(userTechAuthVO.getCategoryId());
        userTechAuthVO.setCategory(category);
        //游戏标签组
        List<TagVO> groupTags = findAllCategoryTagSelected(userTechAuthVO.getCategoryId(),userTechAuthVO.getId(),Boolean.FALSE);
        userTechAuthVO.setGroupTags(groupTags);
        //段位和大区
        List<TechAttrVO> groupAttrs = findAllCategoryAttrSelected(userTechAuthVO.getCategoryId(),userTechAuthVO.getId(),Boolean.FALSE);
        userTechAuthVO.setGroupAttrs(groupAttrs);

        return userTechAuthVO;
    }

    private List<TechAttrVO> findAllCategoryAttrSelected(int categoryId, Integer userTechAuthId,Boolean ignoreNotUser){
        List<TechAttr> techAttrList = techAttrService.findByCategory(categoryId);
        List<UserTechInfo> userTechInfoList = userTechInfoService.findByTechAuthId(userTechAuthId);
        List<TechAttrVO> techAttrVOList = new ArrayList<>();
        for(TechAttr techAttr : techAttrList){
            TechAttrVO techAttrVO = new TechAttrVO();
            BeanUtil.copyProperties(techAttr, techAttrVO);
            List<TechValue> techValueList = techValueService.findByTechAttrId(techAttrVO.getId());
            List<TechValueVO> techValueVOList = CollectionUtil.copyNewCollections(techValueList, TechValueVO.class);
            ListIterator<TechValueVO> techValueVOListIt =  techValueVOList.listIterator();
            while (techValueVOListIt.hasNext()){
                TechValueVO techValueVO = techValueVOListIt.next();
                if(isUserSelectTechValue(userTechInfoList,techValueVO)){
                    techValueVO.setSelected(true);
                }else{
                    techValueVO.setSelected(false);
                    if(ignoreNotUser){
                        techValueVOListIt.remove();
                    }
                }
            }
            techAttrVO.setTechValueVOList(techValueVOList);
            techAttrVOList.add(techAttrVO);
        }
        return techAttrVOList;
    }

    private List<TagVO> findAllCategoryTagSelected(int categoryId, Integer userTechAuthId,Boolean ignoreNotUser) {
        List<TechTag> techTagList = techTagService.findByTechAuthId(userTechAuthId);
        List<Tag> categoryTags = tagService.findAllCategoryTags(categoryId);
        List<TagVO> groupTags = CollectionUtil.copyNewCollections(categoryTags,TagVO.class);
        for(TagVO groupTag : groupTags){
           List<Tag> sonTags = tagService.findByPid(groupTag.getId());
           List<TagVO> sonTagVos = CollectionUtil.copyNewCollections(sonTags,TagVO.class);
           Iterator<TagVO> sonTagVosIt = sonTagVos.iterator();
           while (sonTagVosIt.hasNext()){
               TagVO sonTag = sonTagVosIt.next();
               if(isUserSelectTechTag(techTagList,sonTag)){
                   sonTag.setSelected(true);
               }else{
                   sonTag.setSelected(false);
                   if(ignoreNotUser){
                       sonTagVosIt.remove();
                   }
               }
           }
           groupTag.setSonTags(sonTagVos);
        }
        return groupTags;
    }


    private Boolean isUserSelectTechTag(List<TechTag> techTagList, Tag tag) {
        for (TechTag techTag : techTagList) {
            if (techTag.getTagId().equals(tag.getId())) {
                return true;
            }
        }
        return false;
    }

    private Boolean isUserSelectTechValue(List<UserTechInfo> userTechInfoList, TechValue techValue) {
        for (UserTechInfo userTechInfo : userTechInfoList) {
            if (userTechInfo.getTechValueId().equals(techValue.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询用户可用的技能
     * @param userId
     * @return
     */
    public List<UserTechAuth> findUserNormalTechs(Integer userId){
        List<UserTechAuth> techAuthList = findByStatusAndUserId(userId, TechAuthStatusEnum.NORMAL.getType());
        return techAuthList;
    }

    @Override
    public List<Integer> findUserNormalCategoryIds(Integer userId) {
        List<Integer> categoryIds = new ArrayList<>();
        List<UserTechAuth> techAuthList = findUserNormalTechs(userId);
        for(UserTechAuth techAuth :techAuthList){
            categoryIds.add(techAuth.getCategoryId());
        }
        return categoryIds;
    }

    /**
     * 查询用户段位信息
     * @param techAuthId
     * @return
     */
    public UserTechInfo findDanInfo(Integer techAuthId) {
        List<UserTechInfo> userTechInfoList = userTechInfoService.findByTechAuthId(techAuthId);
        for(UserTechInfo techInfo : userTechInfoList){
            TechAttr techAttr = techAttrService.findById(techInfo.getTechAttrId());
            if(techAttr.getType().equals(TechAttrTypeEnum.DAN.getType())){
                return techInfo;
            }
        }
        return null;
    }


    public void checkUserTechAuth(Integer techAuthId){
        UserTechAuth userTechAuth = findById(techAuthId);
        if(userTechAuth.getStatus().equals(TechAuthStatusEnum.AUTHENTICATION_ING.getType())){
            throw new UserAuthException(UserAuthException.ExceptionCode.USER_TECH_AUTHENTICATION_ING);
        }
        if(userTechAuth.getStatus().equals(TechAuthStatusEnum.NO_AUTHENTICATION.getType())){
            throw new UserAuthException(UserAuthException.ExceptionCode.USER_TECH_NO_AUTHENTICATION);
        }
        if(userTechAuth.getStatus().equals(TechAuthStatusEnum.FREEZE.getType())){
            throw new UserAuthException(UserAuthException.ExceptionCode.USER_TECH_FREEZE);
        }
    }


    @Override
    public List<UserTechAuth> findNormalByCategory(int categoryId) {
        UserTechAuthVO param = new UserTechAuthVO();
        param.setCategoryId(categoryId);
        param.setStatus(TechAuthStatusEnum.NORMAL.getType());
        return userTechAuthDao.findByParameter(param);
    }


    /**
     * 通过用户Id查询用户技能认证信息
     * @param userId
     * @return
     */
    @Override
    public List<UserTechAuth> findByUserId(int userId) {
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
    private void saveTechTag(Integer techAuthId, Integer[] tags) {
        if (tags == null) {
            return;
        }
        List<TechTag> techTagList = techTagService.findByTechAuthId(techAuthId);
        List<Integer> tagList = new ArrayList<>(Arrays.asList(tags));
        for(TechTag techTag : techTagList){
            if(!tagList.contains(techTag.getId())){
                techTagService.deleteById(techTag.getId());
            }else {
                tagList.remove(techTag.getTagId());
            }
        }
        for (Integer tagId : tagList) {
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
     * @param techAuthId
     * @param categoryId
     * @param attrId
     */
    private void saveTechAttr(Integer techAuthId, Integer categoryId, Integer attrId) {
        if (attrId == null) {
            return;
        }
        userTechInfoService.deleteByTechAuthId(techAuthId);
        TechAttr techAttr = techAttrService.findByCategoryAndType(categoryId, TechAttrTypeEnum.DAN.getType());
        TechValue techValue = techValueService.findById(attrId);
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


    private void saveTechAttr(UserTechAuthTO userTechAuthTO) {
        if(userTechAuthTO.getAttrId()==null&&userTechAuthTO.getDanId()==null){
            return;
        }
        if(userTechAuthTO.getAttrId()==null&&userTechAuthTO.getDanId()!=null){
            saveTechAttr(userTechAuthTO.getId(),userTechAuthTO.getCategoryId(),userTechAuthTO.getDanId());
            return;
        }
        //
        List<Integer> attrIds = new ArrayList<>(Arrays.asList(userTechAuthTO.getAttrId()));
        List<UserTechInfo> userTechInfos =  userTechInfoService.findByTechAuthId(userTechAuthTO.getId());
        for(UserTechInfo userTechInfo :userTechInfos){
            if(!attrIds.contains(userTechInfo.getTechValueId())){
                userTechInfoService.deleteById(userTechInfo.getId());
            }else{
                log.info("attrIds:{}",attrIds);
                log.info("userTechInfo:{}",userTechInfo);
                attrIds.remove(userTechInfo.getTechValueId());
            }
        }
        for(Integer attrId : attrIds){
            TechValue techValue = techValueService.findById(attrId);
            if(techValue==null){
                throw new ServiceErrorException("大区不存在,请重新选择!");
            }
            TechAttr techAttr = techAttrService.findById(techValue.getTechAttrId());
            UserTechInfo userTechInfo = new UserTechInfo();
            userTechInfo.setTechAttrId(techAttr.getId());
            userTechInfo.setAttr(techAttr.getName());
            userTechInfo.setTechAuthId(userTechAuthTO.getId());
            userTechInfo.setTechValueId(techValue.getId());
            userTechInfo.setValue(techValue.getName());
            userTechInfo.setRank(techValue.getRank());
            userTechInfo.setStatus(true);
            userTechInfo.setCreateTime(new Date());
            userTechInfo.setUpdateTime(new Date());
            userTechInfoService.create(userTechInfo);
        }


    }


}
