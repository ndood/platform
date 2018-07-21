package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserInfoAuthDao;
import com.fulu.game.core.dao.UserInfoAuthFileTempDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.UserInfoAuthTO;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class UserInfoAuthServiceImpl extends AbsCommonService<UserInfoAuth, Integer> implements UserInfoAuthService {

    @Autowired
    private UserInfoAuthDao userInfoAuthDao;
    @Autowired
    private UserInfoAuthRejectService userInfoAuthRejectService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoFileService userInfoFileService;
    @Autowired
    private UserInfoAuthFileService userInfoAuthFileService;
    @Autowired
    private PersonTagService personTagService;
    @Autowired
    private TagService tagService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;
    @Autowired
    private UserInfoAuthFileTempDao userInfoAuthFileTempDao;
    @Autowired
    private UserInfoAuthFileTempService userInfoAuthFileTempService;

    @Override
    public ICommonDao<UserInfoAuth, Integer> getDao() {
        return userInfoAuthDao;
    }

    public UserInfoAuth findByUserId(int userId) {
        UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
        userInfoAuthVO.setUserId(userId);
        List<UserInfoAuth> userInfoAuthList = userInfoAuthDao.findByParameter(userInfoAuthVO);
        if (CollectionUtil.isEmpty(userInfoAuthList)) {
            return null;
        }
        return userInfoAuthList.get(0);
    }

    /**
     * 保存用户认证的个人信息
     * @param userInfoAuthTO
     * @return
     */
    @Override
    public UserInfoAuth save(UserInfoAuthTO userInfoAuthTO) {
        log.info("保存用户认证信息:UserInfoAuthTO:{}", userInfoAuthTO);
        User user = userService.findById(userInfoAuthTO.getUserId());
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE);
        }
        if (userInfoAuthTO.getMobile() == null) {
            userInfoAuthTO.setMobile(user.getMobile());
        }
        user.setGender(userInfoAuthTO.getGender());
        user.setAge(userInfoAuthTO.getAge());
        user.setBirth(userInfoAuthTO.getBirth());
        user.setConstellation(userInfoAuthTO.getConstellation());
        user.setUserInfoAuth(UserInfoAuthStatusEnum.ALREADY_PERFECT.getType());
        user.setUpdateTime(new Date());
        userService.update(user);

        UserInfoAuth userInfoAuth = new UserInfoAuth();
        BeanUtil.copyProperties(userInfoAuthTO, userInfoAuth);
        userInfoAuth.setUpdateTime(new Date());
        //主图不存userInfoAuth，改为存入临时表
        userInfoAuth.setMainPicUrl(null);
        if (userInfoAuth.getId() == null) {
            UserInfoAuth existUserAuth = findByUserId(user.getId());
            if (existUserAuth != null) {
                throw new UserAuthException(UserAuthException.ExceptionCode.EXIST_USER_AUTH);
            }
            userInfoAuth.setIsRejectSubmit(false);
            userInfoAuth.setCreateTime(new Date());
            userInfoAuth.setPushTimeInterval(30F);
            userInfoAuth.setAllowExport(true);
            create(userInfoAuth);
        }else {
            update(userInfoAuth);
        }

        //删除临时表陈旧数据
        userInfoAuthFileTempService.deleteByUserId(user.getId());
        //添加用户主图
        createUserMainPic(userInfoAuthTO.getMainPicUrl(), user.getId());
        //添加用户认证写真图片
        createUserAuthPortrait(userInfoAuthTO.getPortraitUrls(), user.getId());
        //添加语音介绍
        createUserAuthVoice(userInfoAuthTO.getVoiceUrl(), user.getId(), userInfoAuthTO.getDuration());
        //添加用户信息标签
        createUserInfoTags(userInfoAuthTO.getTags(), user.getId());

        //同步下架用户该技能商品
//        productService.deleteProductByUser(userInfoAuth.getUserId());
        return userInfoAuth;
    }

    /**
     * 保存用户认证的个人信息
     * @param userInfoAuthTO
     * @return
     */
//    @Override
//    public UserInfoAuth save(UserInfoAuthTO userInfoAuthTO) {
//        log.info("保存用户认证信息:UserInfoAuthTO:{}", userInfoAuthTO);
//        User user = userService.findById(userInfoAuthTO.getUserId());
//        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
//            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE);
//        }
//        if (userInfoAuthTO.getMobile() == null) {
//            userInfoAuthTO.setMobile(user.getMobile());
//        }
//        user.setGender(userInfoAuthTO.getGender());
//        user.setAge(userInfoAuthTO.getAge());
//        user.setBirth(userInfoAuthTO.getBirth());
//        user.setConstellation(userInfoAuthTO.getConstellation());
//        user.setUserInfoAuth(UserInfoAuthStatusEnum.ALREADY_PERFECT.getType());
//        user.setUpdateTime(new Date());
//        userService.update(user);
//        //修改认证信息
//        UserInfoAuth userInfoAuth = new UserInfoAuth();
//        BeanUtil.copyProperties(userInfoAuthTO, userInfoAuth);
//        userInfoAuth.setUpdateTime(new Date());
//        if (userInfoAuth.getId() == null) {
//            UserInfoAuth existUserAuth = findByUserId(user.getId());
//            if (existUserAuth != null) {
//                throw new UserAuthException(UserAuthException.ExceptionCode.EXIST_USER_AUTH);
//            }
//            userInfoAuth.setIsRejectSubmit(false);
//            userInfoAuth.setCreateTime(new Date());
//            userInfoAuth.setPushTimeInterval(30F);
//            userInfoAuth.setAllowExport(true);
//            create(userInfoAuth);
//        }else {
//            update(userInfoAuth);
//        }
//        //添加用户认证写真图片
//        createUserAuthPortrait(userInfoAuthTO.getPortraitUrls(), userInfoAuth.getId());
//        //添加语音介绍
//        createUserAuthVoice(userInfoAuthTO.getVoiceUrl(), userInfoAuth.getId(), userInfoAuthTO.getDuration());
//        //添加用户信息标签
//        createUserInfoTags(userInfoAuthTO.getTags(), user.getId());
//
//        //同步下架用户该技能商品
//        productService.deleteProductByUser(userInfoAuth.getUserId());
//        return userInfoAuth;
//    }



    /**
     * 认证信息驳回
     * @param id
     * @param reason
     * @return
     */
    @Override
    public UserInfoAuth reject(int id, String reason) {
        Admin admin = adminService.getCurrentUser();
        log.info("驳回用户个人认证信息:adminId:{};adminName:{};authInfoId:{},reason:{}", admin.getId(), admin.getName(), id, reason);
        //修改认证驳回状态
        UserInfoAuth userInfoAuth = findById(id);
        if (userInfoAuth == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }

        userInfoAuth.setIsRejectSubmit(true);
        update(userInfoAuth);
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        //如果是用户冻结状态给错误提示
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE_ADMIN);
        }
        user.setUserInfoAuth(UserInfoAuthStatusEnum.NOT_PERFECT.getType());
        user.setUpdateTime(new Date());
        userService.update(user);
        //添加驳回理由
        UserInfoAuthReject userInfoAuthReject = new UserInfoAuthReject();
        userInfoAuthReject.setReason(reason);
        userInfoAuthReject.setUserInfoAuthStatus(user.getUserInfoAuth());
        userInfoAuthReject.setUserId(userInfoAuth.getUserId());
        userInfoAuthReject.setAdminId(admin.getId());
        userInfoAuthReject.setAdminName(admin.getName());
        userInfoAuthReject.setUserInfoAuthId(id);
        userInfoAuthReject.setCreateTime(new Date());
        userInfoAuthRejectService.create(userInfoAuthReject);
        //同步下架用户该技能商品
//        productService.deleteProductByUser(userInfoAuth.getUserId());

        //给用户推送通知
        wxTemplateMsgService.pushWechatTemplateMsg(user.getId(), WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT);
        return userInfoAuth;
    }

    /**
     * 技能审核通过
     * @param id
     * @return
     */
    @Override
    public UserInfoAuth pass(int id) {
        Admin admin = adminService.getCurrentUser();
        log.info("清除用户认证信息驳回状态:adminId:{};adminName:{};authInfoId:{}", admin.getId(), admin.getName(), id);
        UserInfoAuth userInfoAuth = findById(id);
        userInfoAuth.setIsRejectSubmit(false);
        update(userInfoAuth);
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        //如果是用户冻结状态给错误提示
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE_ADMIN);
        }
        user.setUserInfoAuth(UserInfoAuthStatusEnum.VERIFIED.getType());
        user.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        userService.update(user);

        Integer userId = userInfoAuth.getUserId();
        UserInfoAuthFileTempVO tempVO = new UserInfoAuthFileTempVO();
        tempVO.setUserId(userId);
        List<UserInfoAuthFileTemp> fileTempList = userInfoAuthFileTempDao.findByParameter(tempVO);
        if(CollectionUtil.isNotEmpty(fileTempList)) {
            //更新主图
            for(UserInfoAuthFileTemp fileTemp : fileTempList) {
                Integer type = fileTemp.getType();
                if(type.equals(FileTypeEnum.MAIN_PIC.getType())) {
                    UserInfoAuth infoAuth = new UserInfoAuth();
                    infoAuth.setId(id);
                    infoAuth.setUserId(userId);
                    infoAuth.setMainPicUrl(fileTemp.getUrl());
                    userInfoAuthDao.update(infoAuth);
                }
            }
            //更新写真图
            deleteTempFileAndUpdateOrg(userId);
            //更新声音文件
//            deleteTempFileAndUpdateOrg(userId, FileTypeEnum.VOICE.getType());

            userInfoAuthFileTempDao.deleteByUserId(userId);
        }

        //同步恢复用户正确技能的商品状态
//        List<UserTechAuth> userTechAuthList = userTechAuthService.findUserNormalTechs(userInfoAuth.getUserId());
//        for (UserTechAuth userTechAuth : userTechAuthList) {
//            productService.recoverProductDelFlagByTechAuthId(userTechAuth.getId());
//        }

        //给用户推送通知
        wxTemplateMsgService.pushWechatTemplateMsg(user.getId(), WechatTemplateMsgEnum.USER_AUTH_INFO_PASS);
        return userInfoAuth;
    }

    /**
     * 将temp表的数据更新到t_user_info_auth_file表
     * @param userId
     */
    public void deleteTempFileAndUpdateOrg(Integer userId) {
        UserInfoAuth auth = findByUserId(userId);

        UserInfoAuthFileTempVO fileTempVO = new UserInfoAuthFileTempVO();
        fileTempVO.setUserId(userId);
        List<UserInfoAuthFileTemp> fileTempList = userInfoAuthFileTempDao.findByParameter(fileTempVO);
        if(CollectionUtil.isEmpty(fileTempList)) {
            return;
        }
        List<String> portraitUrls = new ArrayList<>();
        UserInfoAuthFileTemp voiceFileTemp = new UserInfoAuthFileTemp();
        for(UserInfoAuthFileTemp fileTemp : fileTempList) {
            if(fileTemp.getType().equals(FileTypeEnum.PIC.getType())) {
                portraitUrls.add(fileTemp.getUrl());
            }else if(fileTemp.getType().equals(FileTypeEnum.VOICE.getType())) {
                voiceFileTemp = fileTemp;
            }
        }

        //激活并创建写真图集
        createAndActivateUserAuthPortrait(portraitUrls, auth.getId());
        //激活并创建声音文件
        createAndActivateUserAuthVoice(voiceFileTemp, auth.getId());


//        UserInfoAuthFileTempVO fileTempVO = new UserInfoAuthFileTempVO();
//        fileTempVO.setUserId(userId);
//        fileTempVO.setType(fileType);
//        List<UserInfoAuthFileTemp> fileTemps = userInfoAuthFileTempDao.findByParameter(fileTempVO);
//        for(UserInfoAuthFileTemp meta : fileTemps) {
//            UserInfoAuthFile authFile = new UserInfoAuthFile();
//            BeanUtil.copyProperties(meta, authFile);
//            String activatedUrl = ossUtil.activateOssFile(authFile.getUrl());
//            authFile.setUrl(activatedUrl);
//            authFile.setInfoAuthId(auth.getId());
//            userInfoAuthFileService.create(authFile);
//        }
    }

    /**
     * 认证信息冻结
     *
     * @param id
     * @param reason
     * @return
     */
    @Override
    public UserInfoAuth freeze(int id, String reason) {
        Admin admin = adminService.getCurrentUser();
        log.info("冻结用户个人认证信息:adminId:{};adminName:{};authInfoId:{},reason:{}", admin.getId(), admin.getName(), id, reason);
        UserInfoAuth userInfoAuth = findById(id);
        if (userInfoAuth == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        user.setUserInfoAuth(UserInfoAuthStatusEnum.FREEZE.getType());
        user.setUpdateTime(new Date());
        userService.update(user);

        //添加驳回理由
        UserInfoAuthReject userInfoAuthReject = new UserInfoAuthReject();
        userInfoAuthReject.setReason(reason);
        userInfoAuthReject.setUserInfoAuthStatus(user.getUserInfoAuth());
        userInfoAuthReject.setUserId(userInfoAuth.getUserId());
        userInfoAuthReject.setUserInfoAuthId(id);
        userInfoAuthReject.setAdminId(admin.getId());
        userInfoAuthReject.setAdminName(admin.getName());
        userInfoAuthReject.setCreateTime(new Date());
        userInfoAuthRejectService.create(userInfoAuthReject);

        //下架该用户上传的所有商品
        productService.deleteProductByUser(userInfoAuth.getUserId());
        return userInfoAuth;
    }

    @Override
    public UserInfoAuth unFreeze(int id) {
        Admin admin = adminService.getCurrentUser();
        log.info("解冻用户个人认证信息:adminId:{};adminName:{};authInfoId:{};", admin.getId(), admin.getName(), id);
        UserInfoAuth userInfoAuth = findById(id);
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        user.setUserInfoAuth(UserInfoAuthStatusEnum.VERIFIED.getType());
        user.setUpdateTime(new Date());
        userService.update(user);
        //同步恢复用户正确技能的商品状态
        List<UserTechAuth> userTechAuthList = userTechAuthService.findUserNormalTechs(userInfoAuth.getUserId());
        for (UserTechAuth userTechAuth : userTechAuthList) {
            productService.recoverProductDelFlagByTechAuthId(userTechAuth.getId());
        }

        return userInfoAuth;
    }


    @Override
    public UserInfoAuthVO findUserInfoAuthByUserId(int userId) {
        User user = userService.findById(userId);
        UserInfoAuth userInfoAuth = findByUserId(userId);
        if (userInfoAuth == null) {
            userInfoAuth = new UserInfoAuth();
            userInfoAuth.setUserId(userId);
        }

        UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
        BeanUtil.copyProperties(userInfoAuth, userInfoAuthVO);
        userInfoAuthVO.setNickname(user.getNickname());
        userInfoAuthVO.setAge(user.getAge());
        userInfoAuthVO.setHeadUrl(user.getHeadPortraitsUrl());
        userInfoAuthVO.setGender(user.getGender());
        userInfoAuthVO.setConstellation(user.getConstellation());
        userInfoAuthVO.setBirth(user.getBirth());
        userInfoAuthVO.setUserInfoAuth(user.getUserInfoAuth());
        //查询写真信息和声音
        Integer userInfoAuthStatus = user.getUserInfoAuth();
        //审核中
        if(userInfoAuthStatus.equals(UserInfoAuthStatusEnum.ALREADY_PERFECT.getType())) {
            findUserAuthInfoByTemp(userInfoAuthVO);
            //审核通过或者冻结
        }else if(userInfoAuthStatus.equals(UserInfoAuthStatusEnum.VERIFIED.getType())
                || userInfoAuthStatus.equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            findUserPortraitsAndVoices(userInfoAuthVO);
            //不通过
        }else if(userInfoAuthStatus.equals(UserInfoAuthStatusEnum.NOT_PERFECT.getType())) {
            boolean flag = findUserAuthInfoByTemp(userInfoAuthVO);
            if(!flag) {
                findUserPortraitsAndVoices(userInfoAuthVO);
            }
        }

        //查询用户所有标签
        List<TagVO> allPersonTagVos = findAllUserTagSelected(userId, false);
        userInfoAuthVO.setGroupTags(allPersonTagVos);
        return userInfoAuthVO;
    }


    @Override
    public List<UserInfoAuth> findByUserIds(List<Integer> userIds) {
        if (userIds == null) {
            return new ArrayList<>();
        }
        return userInfoAuthDao.findByUserIds(userIds);
    }



    @Override
    public UserInfoVO findUserCardByUserId(int userId, Boolean hasPhotos, Boolean hasVoice, Boolean hasTags, Boolean hasTechs) {
        User user = userService.findById(userId);
        if (null == user) {
            log.error("用户不存在:{}", userId);
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(userId);
        userInfo.setType(user.getType());
        userInfo.setAge(user.getAge());
        userInfo.setCity(user.getCity());
        userInfo.setHeadUrl(user.getHeadPortraitsUrl());
        userInfo.setNickName(user.getNickname());
        userInfo.setGender(user.getGender());
        userInfo.setScoreAvg(user.getScoreAvg());
        userInfo.setImId(user.getImId());
        userInfo.setImPsw(user.getImPsw());
        int orderCount = orderService.allOrderCount(userId);//接单数
        userInfo.setOrderCount(orderCount);

        UserInfoAuth userInfoAuth = findByUserId(userId);
        if (null != userInfoAuth) {
            userInfo.setMainPhotoUrl(userInfoAuth.getMainPicUrl());
            //查询用户写真图
            if (hasPhotos) {
                List<String> photos = new ArrayList<>();
                List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuth.getId(), FileTypeEnum.PIC.getType());
                for (UserInfoAuthFile authFile : portraitFiles) {
                    photos.add(authFile.getUrl());
                }
                userInfo.setPhotos(photos);
            }
            //查询用户声音
            if (hasVoice) {
                List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuth.getId(), FileTypeEnum.VOICE.getType());
                for (UserInfoAuthFile authFile : voiceFiles) {
                    userInfo.setVoice(authFile.getUrl());
                    userInfo.setVoiceDuration(authFile.getDuration());
                }
            }
        }
        //查询用户标签
        if (hasTags) {
            List<PersonTag> personTagList = personTagService.findByUserId(userId);
            List<String> tags = new ArrayList<>();
            for (PersonTag personTag : personTagList) {
                tags.add(personTag.getName());
            }
            userInfo.setTags(tags);
        }

        //查询用户技能
        if (hasTechs) {
            List<UserTechAuth> userTechAuthList = userTechAuthService.findUserNormalTechs(userId);
            List<String> techList = new ArrayList<String>();
            for (UserTechAuth userTechAuth : userTechAuthList) {
                techList.add(userTechAuth.getCategoryName());
            }
            userInfo.setTechs(techList);
        }
        return userInfo;
    }

    @Override
    public UserInfoVO findUserTechCardByUserId(int techAuthId) {
        //查询认证的技能
        UserInfoVO userInfo = new UserInfoVO();
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(techAuthId,null);
        if (null == userTechAuthVO) {
            throw new UserException(UserException.ExceptionCode.TECH_AUTH_NOT_EXIST_EXCEPTION);
        }
        Integer userId = userTechAuthVO.getUserId();
        userInfo.setUserTechAuthVO(userTechAuthVO);
        User user = userService.findById(userId);
        userInfo.setUserId(userId);
        userInfo.setType(user.getType());
        userInfo.setAge(user.getAge());
        userInfo.setNickName(user.getNickname());
        userInfo.setGender(user.getGender());
        //个人主图
        UserInfoAuth userInfoAuth = findByUserId(userId);
        if (null != userInfoAuth) {
            userInfo.setMainPhotoUrl(userInfoAuth.getMainPicUrl());
        }
        //查询个人标签
        List<PersonTag> personTagList = personTagService.findByUserId(userId);
        List<String> tagList = new ArrayList<>();
        for (PersonTag personTag : personTagList) {
            tagList.add(personTag.getName());
        }
        userInfo.setTags(tagList);
        return userInfo;
    }

    @Override
    public UserInfoVO getSharePage(int techAuthId) {
        UserInfoVO userInfo = new UserInfoVO();
        //查询认证的技能
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(techAuthId,null);
        userInfo.setUserTechAuthVO(userTechAuthVO);
        Integer userId = userTechAuthVO.getUserId();
        //陪玩师个人信息
        User user = userService.findById(userId);
        userInfo.setUserId(userId);
        userInfo.setType(user.getType());
        userInfo.setAge(user.getAge());
        userInfo.setNickName(user.getNickname());
        userInfo.setGender(user.getGender());
        userInfo.setCity(user.getCity());
        //个人写真
        UserInfoAuth userInfoAuth = findByUserId(userId);
        if (null != userInfoAuth) {
            userInfo.setMainPhotoUrl(userInfoAuth.getMainPicUrl());
            List<String> photos = new ArrayList<>();
            photos.add(userInfoAuth.getMainPicUrl());
            List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuth.getId(), FileTypeEnum.PIC.getType());
            for (UserInfoAuthFile authFile : portraitFiles) {
                photos.add(authFile.getUrl());
            }
            userInfo.setPhotos(photos);
        }

        //个人标签
        List<PersonTag> personTagList = personTagService.findByUserId(userId);
        List<String> tags = new ArrayList<>();
        for (PersonTag personTag : personTagList) {
            tags.add(personTag.getName());
        }
        userInfo.setTags(tags);
        return userInfo;
    }


    @Override
    public void settingPushTimeInterval(float minute) {
        //设置时间间隔
        User user = userService.getCurrentUser();
        UserInfoAuth userInfoAuth = findByUserId(user.getId());
        if (userInfoAuth == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }
        userInfoAuth.setPushTimeInterval(minute);
        userInfoAuth.setUpdateTime(new Date());
        update(userInfoAuth);
    }


    @Override
    public PageInfo<UserInfoAuthVO> list(Integer pageNum,
                                         Integer pageSize,
                                         UserInfoAuthSearchVO userInfoAuthSearchVO) {
        List<UserInfoAuthVO> userInfoAuthVOList = new ArrayList<>();
        if (StringUtils.isBlank(userInfoAuthSearchVO.getOrderBy())) {
            userInfoAuthSearchVO.setOrderBy("uia.update_time desc");
        }
        PageHelper.startPage(pageNum, pageSize, userInfoAuthSearchVO.getOrderBy());
        List<UserInfoAuth> userInfoAuths = userInfoAuthDao.findBySearchVO(userInfoAuthSearchVO);
        for (UserInfoAuth userInfoAuth : userInfoAuths) {
            UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
            BeanUtil.copyProperties(userInfoAuth, userInfoAuthVO);
            //查询写真信息和声音
            Integer userInfoAuthStatus = userInfoAuthVO.getUserInfoAuth();
            boolean flag = userInfoAuthStatus.equals(UserInfoAuthStatusEnum.VERIFIED.getType())
                    || userInfoAuthStatus.equals(UserInfoAuthStatusEnum.FREEZE.getType());
            if(flag) {
                findUserPortraitsAndVoices(userInfoAuthVO);
            }else {
                //如果是未通过和审核中的用户信息，从副本表查询
                findUserAuthInfoByTemp(userInfoAuthVO);
            }

            List<TagVO> allPersonTagVos = findAllUserTagSelected(userInfoAuthVO.getUserId(), Boolean.TRUE);
            userInfoAuthVO.setGroupTags(allPersonTagVos);
            //查询用户认证的所有技能
            List<UserTechAuth> userTechAuthList = userTechAuthService.findByUserId(userInfoAuth.getUserId());
            userInfoAuthVO.setUserTechAuthList(userTechAuthList);

            userInfoAuthVOList.add(userInfoAuthVO);
        }
        PageInfo page = new PageInfo(userInfoAuths);
        page.setList(userInfoAuthVOList);
        return page;
    }


    /**
     * 查询用户写真和声音
     * @param userInfoAuthVO
     */
    private void findUserPortraitsAndVoices(UserInfoAuthVO userInfoAuthVO) {
        if (userInfoAuthVO.getId() != null) {
            List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.PIC.getType());
            userInfoAuthVO.setPortraitList(portraitFiles);
            List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.VOICE.getType());
            userInfoAuthVO.setVoiceList(voiceFiles);
        }
    }

    /**
     * 从副本表查询用户认证信息
     * @param userInfoAuthVO
     */
    private boolean findUserAuthInfoByTemp(UserInfoAuthVO userInfoAuthVO) {
        Integer userId = userInfoAuthVO.getUserId();
        if(userId == null) {
            return false;
        }

        //主图
        String mainPic;
        UserInfoAuthFileTempVO tempVO = new UserInfoAuthFileTempVO();
        tempVO.setUserId(userInfoAuthVO.getUserId());
        tempVO.setType(FileTypeEnum.MAIN_PIC.getType());
        List<UserInfoAuthFileTemp> tempList = userInfoAuthFileTempDao.findByParameter(tempVO);
        if(CollectionUtil.isNotEmpty(tempList)) {
            mainPic = tempList.get(0).getUrl();
            userInfoAuthVO.setMainPicUrl(mainPic);
        }

        //写真图
        tempVO.setType(FileTypeEnum.PIC.getType());
        List<UserInfoAuthFileTemp> portraitFilesList = userInfoAuthFileTempDao.findByParameter(tempVO);
        if(CollectionUtil.isNotEmpty(portraitFilesList)) {
            List<UserInfoAuthFile> fileList = new ArrayList<>();
            for(UserInfoAuthFileTemp fileTemp : portraitFilesList) {
                UserInfoAuthFile file = new UserInfoAuthFile();
                BeanUtil.copyProperties(fileTemp, file);
                fileList.add(file);
            }
            userInfoAuthVO.setPortraitList(fileList);
        }else {
            return false;
        }

        //声音
        tempVO.setType(FileTypeEnum.VOICE.getType());
        List<UserInfoAuthFileTemp> voiceTempList = userInfoAuthFileTempDao.findByParameter(tempVO);
        List<UserInfoAuthFile> voiceList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(voiceTempList)) {
            UserInfoAuthFileTemp voiceTemp =  voiceTempList.get(0);
            UserInfoAuthFile voiceFile = new UserInfoAuthFile();
            BeanUtil.copyProperties(voiceTemp, voiceFile);
            voiceList.add(voiceFile);
        }
        userInfoAuthVO.setVoiceList(voiceList);
        return true;
    }



    /**
     * 查询用户信息所有标签
     * @param userId
     * @return
     */
    private List<TagVO> findAllUserTagSelected(int userId, Boolean ignoreNotUser) {
        List<Tag> allPersonTags = tagService.findAllPersonTags();
        List<TagVO> tagVOList = new ArrayList<>();
        Map<Integer, TagVO> tagVOMap = new HashMap<>();
        for (Tag tag : allPersonTags) {
            if (ObjectUtil.equal(0, tag.getPid())) {
                TagVO groupTag = new TagVO();
                BeanUtil.copyProperties(tag, groupTag);
                tagVOMap.put(tag.getId(), groupTag);
                tagVOList.add(groupTag);
            } else {
                if (tagVOMap.containsKey(tag.getPid())) {
                    TagVO sonTag = new TagVO();
                    BeanUtil.copyProperties(tag, sonTag);
                    if (isUserTag(userId, tag)) {
                        sonTag.setSelected(true);
                    }
                    if (ignoreNotUser) {
                        if (sonTag.isSelected()) {
                            tagVOMap.get(tag.getPid()).getSonTags().add(sonTag);
                        }
                    } else {
                        tagVOMap.get(tag.getPid()).getSonTags().add(sonTag);
                    }
                }
            }
        }
        return tagVOList;
    }

    /**
     * 判断一个标签是否是用户选择的标签
     * @return
     */
    private Boolean isUserTag(Integer userId, Tag tag) {
        List<PersonTag> userPersonTagList = personTagService.findByUserId(userId);
        for (PersonTag personTag : userPersonTagList) {
            if (personTag.getTagId().equals(tag.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加用户身份文件
     * @param userId
     * @param idCardHeadUrl
     * @param idCardEmblemUrl
     * @param idCardHandUrl
     */
    public void createUserIdCard(Integer userId, String idCardHeadUrl, String idCardEmblemUrl, String idCardHandUrl) {
        if (idCardHeadUrl != null) {
            createUserInfoFile(userId, idCardHeadUrl, UserInfoFileTypeEnum.IDCARD_HEAD.getMsg(), UserInfoFileTypeEnum.IDCARD_HEAD.getType());
        }
        if (idCardEmblemUrl != null) {
            createUserInfoFile(userId, idCardEmblemUrl, UserInfoFileTypeEnum.IDCARD_EMBLEM.getMsg(), UserInfoFileTypeEnum.IDCARD_EMBLEM.getType());
        }
        if (idCardHandUrl != null) {
            createUserInfoFile(userId, idCardHandUrl, UserInfoFileTypeEnum.IDCARD_HAND.getMsg(), UserInfoFileTypeEnum.IDCARD_HAND.getType());
        }
    }


    private void createUserInfoFile(Integer userId, String fileUrl, String name, Integer type) {
        List<UserInfoFile> idCardFileList = userInfoFileService.findByUserIdAndType(userId, type);
        boolean flag = true;
        for (UserInfoFile file : idCardFileList) {
            if (file.getUrl().contains(fileUrl)) {
                flag = false;
            } else {
                userInfoFileService.deleteFile(file);
            }
        }
        if (flag) {
            UserInfoFile userInfoFile = new UserInfoFile();
            userInfoFile.setName(name);
            userInfoFile.setUrl(ossUtil.activateOssFile(fileUrl));
            userInfoFile.setUserId(userId);
            userInfoFile.setCreateTime(new Date());
            userInfoFile.setType(type);
            userInfoFileService.create(userInfoFile);
        }
    }

    public void createAndActivateUserAuthVoice(UserInfoAuthFileTemp fileTemp, Integer userInfoAuthId) {
        String voiceUrl = fileTemp.getUrl();
        if(StringUtils.isBlank(voiceUrl)) {
            return;
        }

        List<UserInfoAuthFile> voiceList = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthId,
                FileTypeEnum.VOICE.getType());
        if(CollectionUtil.isEmpty(voiceList)) {
            return;
        }

        String dbVoiceUrl = voiceList.get(0).getUrl();
        if(dbVoiceUrl.equals(voiceUrl)) {
            return;
        }

        UserInfoAuthFile authFile = new UserInfoAuthFile();
        authFile.setId(voiceList.get(0).getId());
        authFile.setUrl(dbVoiceUrl);
        authFile.setInfoAuthId(userInfoAuthId);
        userInfoAuthFileService.deleteFile(authFile);

        UserInfoAuthFile createFile = new UserInfoAuthFile();
        BeanUtil.copyProperties(fileTemp, createFile);
        createFile.setInfoAuthId(userInfoAuthId);
        userInfoAuthFileService.create(createFile);
    }

    /**
     * 添加用户写真图集
     * @param portraitUrlList
     * @param userInfoAuthId
     */
    public void createAndActivateUserAuthPortrait(List<String> portraitUrlList, Integer userInfoAuthId) {
        if (CollectionUtil.isEmpty(portraitUrlList)) {
            return;
        }
        //激活所有写真URL
//        List<String> portraitUrlList = Arrays.asList(portraitUrls);
        for (int i = 0; i < portraitUrlList.size(); i++) {
            portraitUrlList.set(i, ossUtil.activateOssFile(portraitUrlList.get(i)));
        }
        List<UserInfoAuthFile> dbPicFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthId, FileTypeEnum.PIC.getType());
        Iterator<UserInfoAuthFile> dbIt = dbPicFiles.iterator();
        while (dbIt.hasNext()) {
            UserInfoAuthFile file = dbIt.next();
            if (!portraitUrlList.contains(file.getUrl())) {
                userInfoAuthFileService.deleteFile(file);
                dbIt.remove();
            }
        }
        if (CollectionUtil.isEmpty(dbPicFiles)) {
            for (int i = 0; i < portraitUrlList.size(); i++) {
                String portraitUrl = portraitUrlList.get(i);
                UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                userInfoAuthFile.setUrl(portraitUrl);
                userInfoAuthFile.setInfoAuthId(userInfoAuthId);
                userInfoAuthFile.setName("写真" + (i + 1));
                userInfoAuthFile.setCreateTime(new Date());
                userInfoAuthFile.setType(FileTypeEnum.PIC.getType());
                userInfoAuthFileService.create(userInfoAuthFile);
            }
        } else {
            for (int i = 0; i < portraitUrlList.size(); i++) {
                try {
                    UserInfoAuthFile file = dbPicFiles.get(i);
                    if (!Objects.equals(file.getUrl(), portraitUrlList.get(i))) {
                        file.setUrl(portraitUrlList.get(i));
                        userInfoAuthFileService.update(file);
                    }
                } catch (IndexOutOfBoundsException e) {
                    UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                    userInfoAuthFile.setUrl(portraitUrlList.get(i));
                    userInfoAuthFile.setInfoAuthId(userInfoAuthId);
                    userInfoAuthFile.setName("写真" + (i + 1));
                    userInfoAuthFile.setCreateTime(new Date());
                    userInfoAuthFile.setType(FileTypeEnum.PIC.getType());
                    userInfoAuthFileService.create(userInfoAuthFile);
                }
            }
        }
    }

    /**
     * 添加主图
     * @param mainPicUrl
     * @param userId
     */
    public void createUserMainPic(String mainPicUrl, Integer userId) {
        if(StringUtils.isBlank(mainPicUrl)) {
            return;
        }

        UserInfoAuthFileTemp fileTemp = new UserInfoAuthFileTemp();
        fileTemp.setUserId(userId);
        fileTemp.setName("主图");
        fileTemp.setType(FileTypeEnum.MAIN_PIC.getType());
        mainPicUrl = ossUtil.activateOssFile(mainPicUrl);
        fileTemp.setUrl(mainPicUrl);
        fileTemp.setUpdateTime(DateUtil.date());
        fileTemp.setCreateTime(DateUtil.date());
        userInfoAuthFileTempDao.create(fileTemp);
    }

    /**
     * 添加用户写真图集
     * @param portraitUrls
     * @param userId
     */
    public void createUserAuthPortrait(String[] portraitUrls, Integer userId) {
        if (portraitUrls == null || portraitUrls.length == 0) {
            return;
        }
        List<String> portraitUrlList = Arrays.asList(portraitUrls);
        for(int i = 0; i < portraitUrlList.size(); i++) {
            UserInfoAuthFileTemp fileTemp = new UserInfoAuthFileTemp();
            fileTemp.setUserId(userId);
            fileTemp.setName("写真" + (i + 1));
            fileTemp.setType(FileTypeEnum.PIC.getType());
            String portraitUrl = ossUtil.activateOssFile(portraitUrlList.get(i));
            fileTemp.setUrl(portraitUrl);
            fileTemp.setCreateTime(DateUtil.date());
            fileTemp.setUpdateTime(DateUtil.date());
            userInfoAuthFileTempDao.create(fileTemp);
        }
    }


    /**
     * 添加用户认证声音
     *
     * @param voiceUrl
     * @param userId
     * @param duration
     */
    public void createUserAuthVoice(String voiceUrl, Integer userId, Integer duration) {
        if (voiceUrl == null) {
            return;
        }
        UserInfoAuthFileTemp fileTemp = new UserInfoAuthFileTemp();
        fileTemp.setUserId(userId);
        fileTemp.setName("语音介绍");
        fileTemp.setType(FileTypeEnum.VOICE.getType());
        voiceUrl = ossUtil.activateOssFile(voiceUrl);
        fileTemp.setUrl(voiceUrl);
        fileTemp.setDuration(duration);
        fileTemp.setCreateTime(DateUtil.date());
        fileTemp.setUpdateTime(DateUtil.date());
        userInfoAuthFileTempDao.create(fileTemp);
    }

    /**
     * 添加用户标签组
     *
     * @param tags
     * @param userId
     */
    public void createUserInfoTags(Integer[] tags, Integer userId) {
        if (tags == null) {
            return;
        }
        if (tags.length > 0) {
            personTagService.deleteByUserId(userId);
        }
        for (Integer tagId : tags) {
            Tag tag = tagService.findById(tagId);
            PersonTag personTag = new PersonTag();
            personTag.setTagId(tag.getId());
            personTag.setUserId(userId);
            personTag.setName(tag.getName());
            personTag.setCreateTime(new Date());
            personTag.setUpdateTime(new Date());
            personTagService.create(personTag);
        }
    }

}
