package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.FileTypeEnum;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.common.enums.UserInfoFileTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserInfoAuthDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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


    @Override
    public UserInfoAuthVO save(UserInfoAuthVO userInfoAuthVO) {
        log.info("保存用户认证信息:userInfoAuthVO:{}", userInfoAuthVO);

        //更新用户信息
        User user = userService.findById(userInfoAuthVO.getUserId());
        //如果是用户冻结状态给错误提示
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE);
        }
        if (userInfoAuthVO.getMobile() == null) {
            userInfoAuthVO.setMobile(user.getMobile());
        }
        user.setHeadPortraitsUrl(ossUtil.activateOssFile(userInfoAuthVO.getHeadUrl()));
        user.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        user.setIdcard(userInfoAuthVO.getIdCard());
        user.setRealname(userInfoAuthVO.getRealname());
        user.setGender(userInfoAuthVO.getGender());
        user.setUserInfoAuth(UserInfoAuthStatusEnum.VERIFIED.getType());
        userService.update(user);
        //忽略为null的属性
        BeanUtil.CopyOptions copyOptions = BeanUtil.CopyOptions.create();
        copyOptions.setIgnoreNullValue(true);
        //添加认证信息
        UserInfoAuth userInfoAuth = new UserInfoAuth();
        BeanUtil.copyProperties(userInfoAuthVO, userInfoAuth, copyOptions);
        userInfoAuth.setUpdateTime(new Date());
        if (userInfoAuth.getId() == null) {
            UserInfoAuth userAuth = findByUserId(user.getId());
            if (userAuth != null) {
                throw new UserAuthException(UserAuthException.ExceptionCode.EXIST_USER_AUTH);
            }
            userInfoAuth.setIsRejectSubmit(false);
            userInfoAuth.setCreateTime(new Date());
            create(userInfoAuth);
        } else {
            update(userInfoAuth);
            //同步恢复用户正确技能的商品状态
            List<UserTechAuth> userTechAuthList = userTechAuthService.findUserNormalTechs(userInfoAuth.getUserId());
            for (UserTechAuth userTechAuth : userTechAuthList) {
                productService.recoverProductDelFlagByTechAuthId(userTechAuth.getId());
            }
        }
        //添加认证身份证文件
        createUserIdCard(user.getId(), userInfoAuthVO.getIdCardHeadUrl(), userInfoAuthVO.getIdCardEmblemUrl(), userInfoAuthVO.getIdCardHandUrl());
        //添加用户认证写真图片
        createUserAuthPortrait(userInfoAuthVO.getPortraitUrls(), userInfoAuth.getId());
        //添加语音介绍
        createUserAuthVoice(userInfoAuthVO.getVoiceUrl(), userInfoAuth.getId(), userInfoAuthVO.getDuration());
        //添加用户信息标签
        createUserInfoTags(userInfoAuthVO.getTags(), user.getId());
        //更新用户商品索引的信息
        productService.updateUserProductIndex(user.getId(), Boolean.FALSE);
        return userInfoAuthVO;
    }

    /**
     * 认证信息驳回
     *
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
        productService.deleteProductByUser(userInfoAuth.getUserId());
        return userInfoAuth;
    }

    /**
     * 清楚驳回记录状态
     *
     * @param id
     * @return
     */
    @Override
    public UserInfoAuth unReject(int id) {
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
        userService.update(user);

        //同步恢复用户正确技能的商品状态
        List<UserTechAuth> userTechAuthList = userTechAuthService.findUserNormalTechs(userInfoAuth.getUserId());
        for (UserTechAuth userTechAuth : userTechAuthList) {
            productService.recoverProductDelFlagByTechAuthId(userTechAuth.getId());
        }
        return userInfoAuth;
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
    public UserInfoAuthVO findUserAuthInfoByUserId(int userId) {
        User user = userService.findById(userId);
        UserInfoAuth userInfoAuth = findByUserId(userId);
        if (userInfoAuth == null) {
            userInfoAuth = new UserInfoAuth();
            userInfoAuth.setUserId(userId);
        }
        UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
        BeanUtil.copyProperties(userInfoAuth, userInfoAuthVO);
        copyUserInfo2InfoAuthVo(user, userInfoAuthVO);
        userInfoAuthVO.setRealMobile(user.getMobile());
        //查询身份证信息
        List<UserInfoFile> userInfoFileList = userInfoFileService.findByUserId(userId);
        userInfoAuthVO.setIdCardList(userInfoFileList);
        //查询写真信息和声音
        copyAuthFile2InfoAuthVo(userInfoAuthVO);
        List<TagVO> allPersonTagVos = findAllUserTag(userId, false);
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
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(techAuthId);
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
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(techAuthId);
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
    public PageInfo<UserInfoAuthVO> list(Integer pageNum, Integer pageSize, String orderBy, String mobile, String startTime, String endTime) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "update_time desc";
        }
        List<UserInfoAuthVO> userInfoAuthVOList = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize, orderBy);
        //List<UserInfoAuth> userInfoAuths = findAll();
        UserInfoAuthVO requestVo = new UserInfoAuthVO();
        requestVo.setMobile(mobile);
        requestVo.setStartTime(startTime);
        requestVo.setEndTime(endTime);
        List<UserInfoAuth> userInfoAuths = userInfoAuthDao.findByParameter(requestVo);
        for (UserInfoAuth userInfoAuth : userInfoAuths) {
            UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
            BeanUtil.copyProperties(userInfoAuth, userInfoAuthVO);
            User user = userService.findById(userInfoAuthVO.getUserId());
            userInfoAuthVO.setUserInfoAuth(user.getUserInfoAuth());

            copyUserInfo2InfoAuthVo(user, userInfoAuthVO);
            //查询身份证信息
            List<UserInfoFile> userInfoFileList = userInfoFileService.findByUserId(user.getId());
            userInfoAuthVO.setIdCardList(userInfoFileList);
            //查询写真信息和声音
            copyAuthFile2InfoAuthVo(userInfoAuthVO);
            List<TagVO> allPersonTagVos = findAllUserTag(user.getId(), true);
            userInfoAuthVO.setGroupTags(allPersonTagVos);

            userInfoAuthVOList.add(userInfoAuthVO);
        }
        PageInfo page = new PageInfo(userInfoAuths);
        page.setList(userInfoAuthVOList);
        return page;
    }

    private void copyAuthFile2InfoAuthVo(UserInfoAuthVO userInfoAuthVO) {
        if (userInfoAuthVO.getId() != null) {
            List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.PIC.getType());
            userInfoAuthVO.setPortraitList(portraitFiles);
            List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.VOICE.getType());
            userInfoAuthVO.setVoiceList(voiceFiles);
        }
    }


    private void copyUserInfo2InfoAuthVo(User user, UserInfoAuthVO userInfoAuthVO) {
        userInfoAuthVO.setHeadUrl(user.getHeadPortraitsUrl());
        userInfoAuthVO.setIdCard(user.getIdcard());
        userInfoAuthVO.setGender(user.getGender());
        userInfoAuthVO.setRealname(user.getRealname());
        userInfoAuthVO.setAge(user.getAge());
        userInfoAuthVO.setNickname(user.getNickname());
    }

    /**
     * 查询用户信息所有标签
     *
     * @param userId
     * @return
     */
    private List<TagVO> findAllUserTag(int userId, Boolean ignoreNotUser) {
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
     *
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
     *
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

    /**
     * 添加用户写真图集
     *
     * @param portraitUrls
     * @param userInfoAuthId
     */
    public void createUserAuthPortrait(String[] portraitUrls, Integer userInfoAuthId) {
        if (portraitUrls == null || portraitUrls.length == 0) {
            return;
        }
        //激活所有写真URL
        List<String> portraitUrlList = Arrays.asList(portraitUrls);
        for (int i = 0; i < portraitUrlList.size(); i++) {
            portraitUrlList.set(i, ossUtil.activateOssFile(portraitUrlList.get(i)));
        }
        //添加第一张写真图作为主图保存
        UserInfoAuth userInfoAuth = new UserInfoAuth();
        userInfoAuth.setId(userInfoAuthId);
        userInfoAuth.setMainPicUrl(portraitUrlList.get(0));
        update(userInfoAuth);
        List<UserInfoAuthFile> dbPicFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthId, FileTypeEnum.PIC.getType());
        Iterator<UserInfoAuthFile> dbIt = dbPicFiles.iterator();
        while (dbIt.hasNext()){
            UserInfoAuthFile file = dbIt.next();
            if(!portraitUrlList.contains(file.getUrl())){
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
        }else{
            for(int i=0;i<portraitUrlList.size();i++){
                try {
                    UserInfoAuthFile file = dbPicFiles.get(i);
                    if(!Objects.equals(file.getUrl(),portraitUrlList.get(i))){
                        file.setUrl(portraitUrlList.get(i));
                        userInfoAuthFileService.update(file);
                    }
                }catch (IndexOutOfBoundsException e){
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
     * 添加用户认证声音
     *
     * @param voiceUrl
     * @param userInfoAuthId
     */
    public void createUserAuthVoice(String voiceUrl, Integer userInfoAuthId, Integer duration) {
        if (voiceUrl == null) {
            return;
        }
        List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthId, FileTypeEnum.VOICE.getType());
        boolean flag = true;
        for (UserInfoAuthFile file : voiceFiles) {
            if (file.getUrl().contains(voiceUrl)) {
                flag = false;
            } else {
                userInfoAuthFileService.deleteFile(file);
            }
        }
        if (flag) {
            UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
            userInfoAuthFile.setUrl(ossUtil.activateOssFile(voiceUrl));
            userInfoAuthFile.setDuration(duration);
            userInfoAuthFile.setInfoAuthId(userInfoAuthId);
            userInfoAuthFile.setName("语音介绍");
            userInfoAuthFile.setCreateTime(new Date());
            userInfoAuthFile.setType(FileTypeEnum.VOICE.getType());
            userInfoAuthFileService.create(userInfoAuthFile);
        }
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
