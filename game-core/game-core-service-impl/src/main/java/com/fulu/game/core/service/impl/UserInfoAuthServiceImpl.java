package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.FileTypeEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.MailUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Slf4j
public class UserInfoAuthServiceImpl extends AbsCommonService<UserInfoAuth, Integer> implements UserInfoAuthService {

    @Autowired
    private UserInfoAuthDao userInfoAuthDao;

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
    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private UserInfoAuthFileTempDao userInfoAuthFileTempDao;
    @Autowired
    private UserInfoAuthFileTempService userInfoAuthFileTempService;
    @Autowired
    private VirtualProductService virtualProductService;
    @Autowired
    private VirtualProductAttachService virtualProductAttachService;
    @Autowired
    private ProductService productService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Autowired
    private Config configProperties;
    @Autowired
    private PriceRuleService priceRuleService;


    @Override
    public ICommonDao<UserInfoAuth, Integer> getDao() {
        return userInfoAuthDao;
    }

    @Override
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
     *
     * @param userInfoAuthTO
     * @return
     */
    @Override
    @Transactional
    public UserInfoAuth save(UserInfoAuthTO userInfoAuthTO) {
        log.info("保存用户认证信息:UserInfoAuthTO:{}", userInfoAuthTO);
        User user = userService.findById(userInfoAuthTO.getUserId());
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE);
        }
        user.setGender(userInfoAuthTO.getGender());
        user.setAge(userInfoAuthTO.getAge());
        user.setBirth(userInfoAuthTO.getBirth());
        user.setConstellation(userInfoAuthTO.getConstellation());
        user.setUserInfoAuth(UserInfoAuthStatusEnum.ALREADY_PERFECT.getType());
        if (userInfoAuthTO.getScoreAvg() != null) {
            user.setScoreAvg(userInfoAuthTO.getScoreAvg());
        }
        user.setUpdateTime(new Date());
        userService.update(user);


        UserInfoAuth userInfoAuth = new UserInfoAuth();
        BeanUtil.copyProperties(userInfoAuthTO, userInfoAuth);
        userInfoAuth.setUpdateTime(new Date());
//        userInfoAuthDao.update(userInfoAuth);

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
        } else {
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


        String privatePicStr = userInfoAuthTO.getPrivatePicStr();

        //设置陪玩师的私密照
        if (StringUtils.isNotBlank(privatePicStr)) {

            JSONObject privatePicJson = JSONObject.parseObject(privatePicStr);

            //获取需要删除的私密照组
            JSONArray delIds = privatePicJson.getJSONArray("delList");

            for (int i = 0; i < delIds.size(); i++) {
                VirtualProduct t = new VirtualProduct();
                t.setId(delIds.getIntValue(i));
                t.setDelFlag(true);

                virtualProductService.update(t);
            }

            //修改私密照
            JSONArray updateList = privatePicJson.getJSONArray("updateList");
            for (int i = 0; i < updateList.size(); i++) {

                //修改商品信息
                JSONObject groupInfo = updateList.getJSONObject(i);
                VirtualProduct t = new VirtualProduct();
                t.setId(groupInfo.getIntValue("virtualProductId"));
                t.setName(groupInfo.getString("name"));
                t.setPrice(groupInfo.getIntValue("price"));
                t.setSort(groupInfo.getIntValue("sort"));
                t.setAttachCount(groupInfo.getJSONArray("urls").size());
                t.setUpdateTime(new Date());

                virtualProductService.update(t);

                //修改附件信息
                JSONArray urls = groupInfo.getJSONArray("urls");
                //删除旧附件
                virtualProductAttachService.deleteByVirtualProductId(t.getId());
                //添加新的附件信息
                for (int j = 0; j < urls.size(); j++) {
                    VirtualProductAttach vpa = new VirtualProductAttach();
                    vpa.setUserId(userInfoAuthTO.getUserId());
                    vpa.setVirtualProductId(t.getId());
                    vpa.setUrl(ossUtil.activateOssFile(urls.getString(j)));
                    vpa.setCreateTime(new Date());
                    virtualProductAttachService.create(vpa);
                }
            }

            //添加新的私密照
            JSONArray addList = privatePicJson.getJSONArray("addList");
            for (int i = 0; i < addList.size(); i++) {

                //添加商品信息
                JSONObject groupInfo = addList.getJSONObject(i);
                VirtualProduct t = new VirtualProduct();
                t.setName(groupInfo.getString("name"));
                t.setPrice(groupInfo.getIntValue("price"));
                t.setSort(groupInfo.getIntValue("sort"));
                t.setType(VirtualProductTypeEnum.PERSONAL_PICS.getType());
                t.setAttachCount(groupInfo.getJSONArray("urls").size());
                t.setDelFlag(false);
                t.setCreateTime(new Date());

                virtualProductService.create(t);

                //获取附件信息
                JSONArray urls = groupInfo.getJSONArray("urls");
                //添加附件信息
                for (int j = 0; j < urls.size(); j++) {
                    VirtualProductAttach vpa = new VirtualProductAttach();
                    vpa.setUserId(userInfoAuthTO.getUserId());
                    vpa.setVirtualProductId(t.getId());
                    vpa.setUrl(ossUtil.activateOssFile(urls.getString(j)));
                    vpa.setCreateTime(new Date());
                    virtualProductAttachService.create(vpa);
                }
            }
        }

        return userInfoAuth;
    }


    /**
     * 保存用户认证的个人信息的排序号
     *
     * @param userInfoAuthTO
     * @return
     */
    @Override
    public void saveSort(UserInfoAuthTO userInfoAuthTO) {
        userInfoAuthDao.updateUserSort(userInfoAuthTO.getUserId(), userInfoAuthTO.getSort());
    }


    /**
     * 根据用户id，将临时表的文件更新到主表中，并且删除临时表的数据
     *
     * @param userId 用户id
     */
    public void deleteTempFileAndUpdateOrg(Integer userId) {
        UserInfoAuth auth = findByUserId(userId);

        UserInfoAuthFileTempVO fileTempVO = new UserInfoAuthFileTempVO();
        fileTempVO.setUserId(userId);
        List<UserInfoAuthFileTemp> fileTempList = userInfoAuthFileTempDao.findByParameter(fileTempVO);
        if (CollectionUtil.isEmpty(fileTempList)) {
            return;
        }
        List<String> portraitUrls = new ArrayList<>();
        UserInfoAuthFileTemp voiceFileTemp = new UserInfoAuthFileTemp();
        for (UserInfoAuthFileTemp fileTemp : fileTempList) {
            if (fileTemp.getType().equals(FileTypeEnum.PIC.getType())) {
                portraitUrls.add(fileTemp.getUrl());
            } else if (fileTemp.getType().equals(FileTypeEnum.VOICE.getType())) {
                voiceFileTemp = fileTemp;
            }
        }

        createAndActivateUserAuthPortrait(portraitUrls, auth.getId());
        createAndActivateUserAuthVoice(voiceFileTemp, auth.getId());
    }


    @Override
    public UserInfoAuthVO findUserInfoAuthByUserId(Integer userId) {
        if (userId == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }

        User user = userService.findById(userId);
        UserInfoAuth userInfoAuth = findByUserId(userId);
        if (userInfoAuth == null) {
            userInfoAuth = new UserInfoAuth();
            userInfoAuth.setUserId(userId);
        }

        UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
        BeanUtil.copyProperties(userInfoAuth, userInfoAuthVO);
        //设置用户认证扩展信息 add by shijiaoyun
        setUserInfoAuthExtInfo(userInfoAuthVO, user);

        Integer userInfoAuthStatus = user.getUserInfoAuth();
        findUserAuthInfoByStatus(userInfoAuthStatus, userInfoAuthVO);

        //查询用户所有标签
        List<TagVO> allPersonTagVos = findAllUserTagSelected(userId, false);
        userInfoAuthVO.setGroupTags(allPersonTagVos);

        //查询用户的所有私密照片
        VirtualProductAttachVO vpav = new VirtualProductAttachVO();
        vpav.setDelFlag(false);
        vpav.setType(VirtualProductTypeEnum.PERSONAL_PICS.getType());
        vpav.setUserId(userId);

        List<VirtualProductAttachVO> attachList = virtualProductAttachService.findDetailByVo(vpav);

        //将私密照片分组归类
        boolean exitsFlag = false;
        for (int i = 0; i < attachList.size(); i++) {

            exitsFlag = false;

            List<PicGroupVO> groupList = userInfoAuthVO.getGroupList();
            PicGroupVO temp = null;
            for (int j = 0; j < groupList.size(); j++) {
                if (attachList.get(i).getVirtualProductId().intValue() == groupList.get(j).getVirtualProductId().intValue()) {
                    exitsFlag = true;
                    temp = groupList.get(j);
                    break;
                }
            }

            if (exitsFlag) {
                temp.getUrls().add(attachList.get(i).getUrl());
            } else {
                temp = new PicGroupVO();
                temp.setName(attachList.get(i).getName());
                temp.setPrice(attachList.get(i).getPrice());
                temp.setVirtualProductId(attachList.get(i).getVirtualProductId());
                temp.getUrls().add(attachList.get(i).getUrl());
                groupList.add(temp);
            }

        }

        return userInfoAuthVO;
    }

    /**
     * 设置用户认证信息附加信息
     * add by shijiaoyun
     *
     * @param userInfoAuthVO
     */
    private void setUserInfoAuthExtInfo(UserInfoAuthVO userInfoAuthVO, User user) {
        if (userInfoAuthVO == null || userInfoAuthVO.getUserId() == null || user == null) {
            return;
        }
        int userId = user.getId();
        userInfoAuthVO.setNickname(user.getNickname());
        userInfoAuthVO.setAge(user.getAge());
        userInfoAuthVO.setHeadUrl(user.getHeadPortraitsUrl());
        userInfoAuthVO.setGender(user.getGender());
        userInfoAuthVO.setConstellation(user.getConstellation());
        userInfoAuthVO.setBirth(user.getBirth());
        userInfoAuthVO.setUserInfoAuth(user.getUserInfoAuth());
        userInfoAuthVO.setBalance(user.getBalance());
        userInfoAuthVO.setChargeBalance(user.getChargeBalance());

        //添加管理平台新增属性 add by shijiaoyun
        userInfoAuthVO.setScoreAvg(user.getScoreAvg());
        userInfoAuthVO.setRegisterType(user.getRegisterType());
        //获取关注数
        int attentions = redisOpenService.bitCount(RedisKeyEnum.ATTENTION_USERS.generateKey(userId)).intValue();
        //获取粉丝数
        int fans = redisOpenService.bitCount(RedisKeyEnum.ATTENTIONED_USERS.generateKey(userId)).intValue();
        userInfoAuthVO.setAttentions(attentions);
        userInfoAuthVO.setFansCount(fans);
        userInfoAuthVO.setHistoryBrowseCount(redisOpenService.getInteger(RedisKeyEnum.HISTORY_BROWSE_COUNT.generateKey(userId)));
        userInfoAuthVO.setHistoryAccessedCount(redisOpenService.getInteger(RedisKeyEnum.HISTORY_ACCESSED_COUNT.generateKey(userId)));
        userInfoAuthVO.setDynamicCount(redisOpenService.getInteger(RedisKeyEnum.DYNAMIC_COUNT.generateKey(userId)));
        // 新增用户认证图片、语音、和视频文件列表
        List<UserInfoAuthFile> portraitFiles = new ArrayList<>();
        if(userInfoAuthVO != null && userInfoAuthVO.getId() != null){
            portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.PIC.getType());
        }
        userInfoAuthVO.setPortraitList(portraitFiles);
        List<UserInfoAuthFile> voiceFiles = new ArrayList<>();
        if(userInfoAuthVO != null && userInfoAuthVO.getId() != null) {
            voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.VOICE.getType());
        }
        userInfoAuthVO.setVoiceList(voiceFiles);
        //添加视频信息
        List<UserInfoAuthFile> videoFiles = new ArrayList<>();
        if(userInfoAuthVO != null && userInfoAuthVO.getId() != null) {
            videoFiles =userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.VIDEO.getType());
        }
        userInfoAuthVO.setVideoList(videoFiles);
    }

    @Override
    public List<UserInfoAuth> findByUserIds(List<Integer> userIds) {
        return findByUserIds(userIds, null, null);
    }

    @Override
    public List<UserInfoAuth> findByUserIds(List<Integer> userIds, Integer pageNum, Integer pageSize) {
        if (userIds == null) {
            return new ArrayList<>();
        }
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
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
        //接单数
        int orderCount = orderService.allOrderCount(userId);
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
            List<String> techList = new ArrayList<>();
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
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(techAuthId, null);
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
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(techAuthId, null);
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
        String orderBy = userInfoAuthSearchVO.getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "uia.update_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<UserInfoAuthVO> userInfoAuths = userInfoAuthDao.findBySearchVO(userInfoAuthSearchVO);
        for (UserInfoAuth userInfoAuth : userInfoAuths) {
            UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
            BeanUtil.copyProperties(userInfoAuth, userInfoAuthVO);

            Integer userInfoAuthStatus = userInfoAuthVO.getUserInfoAuth();
            findUserAuthInfoByStatus(userInfoAuthStatus, userInfoAuthVO);

            List<TagVO> allPersonTagVos = findAllUserTagSelected(userInfoAuthVO.getUserId(), Boolean.TRUE);
            userInfoAuthVO.setGroupTags(allPersonTagVos);
            //查询用户认证的所有技能
            List<UserTechAuth> userTechAuthList = userTechAuthService.findByUserId(userInfoAuth.getUserId());
            userInfoAuthVO.setUserTechAuthList(userTechAuthList);

            ProductShowCaseVO psv = productService.findRecommendProductByUserId(userInfoAuth.getUserId());
            if (psv != null) {
                userInfoAuthVO.setRecommendProductId(psv.getId());
            }


            //获取每个陪玩师的私密照套数
            VirtualProductVO vpv = new VirtualProductVO();
            vpv.setUserId(userInfoAuthVO.getUserId());
            vpv.setDelFlag(false);
            vpv.setType(VirtualProductTypeEnum.PERSONAL_PICS.getType());

            List<VirtualProductVO> vpList = virtualProductService.findByVirtualProductVo(vpv);
            userInfoAuthVO.setGroupPicCount(vpList.size());

            userInfoAuthVOList.add(userInfoAuthVO);

        }

        PageInfo page = new PageInfo(userInfoAuths);
        page.setList(userInfoAuthVOList);
        return page;
    }

    @Override
    public List<UserInfoAuthVO> findBySearchVO(UserInfoAuthSearchVO userInfoAuthSearchVO) {
        List<UserInfoAuthVO> userInfoAuths = userInfoAuthDao.findBySearchVO(userInfoAuthSearchVO);
        return userInfoAuths;
    }


    @Override
    public boolean updateByUserId(UserInfoAuth userInfoAuth) {
        Integer userId = userInfoAuth.getUserId();
        if (userId == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        int result = userInfoAuthDao.updateByUserId(userInfoAuth);
        return result > 0;
    }

    /**
     * 根据userInfoAuthStatus（陪玩师审核状态）获取陪玩师主图、写真图和声音文件等信息，补充陪玩师认证信息VO
     *
     * @param userInfoAuthStatus 陪玩师审核状态
     * @param userInfoAuthVO     陪玩师认证信息VO
     * @return 陪玩师认证信息VO
     */
    private UserInfoAuthVO findUserAuthInfoByStatus(Integer userInfoAuthStatus, UserInfoAuthVO userInfoAuthVO) {
        if (userInfoAuthStatus == null || userInfoAuthVO == null) {
            log.error("查询参数为空");
            return null;
        }

        Integer userId = userInfoAuthVO.getUserId();
        if (userId == null) {
            log.error("查询参数为空");
            return null;
        }

        //审核中
        if (userInfoAuthStatus.equals(UserInfoAuthStatusEnum.ALREADY_PERFECT.getType())) {
            findUserAuthInfoByTemp(userInfoAuthVO);
            //审核通过或者冻结
        } else if (userInfoAuthStatus.equals(UserInfoAuthStatusEnum.VERIFIED.getType())
                || userInfoAuthStatus.equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            findUserPortraitsAndVoices(userInfoAuthVO);
            //不通过
        } else if (userInfoAuthStatus.equals(UserInfoAuthStatusEnum.NOT_PERFECT.getType())) {
            boolean flag = findUserAuthInfoByTemp(userInfoAuthVO);
            if (!flag) {
                findUserPortraitsAndVoices(userInfoAuthVO);
            }
        }
        return userInfoAuthVO;
    }

    /**
     * 查询用户写真和声音
     *
     * @param userInfoAuthVO
     */
    private void findUserPortraitsAndVoices(UserInfoAuthVO userInfoAuthVO) {
        if (userInfoAuthVO.getId() != null) {
            List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.PIC.getType());
            userInfoAuthVO.setPortraitList(portraitFiles);
            List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.VOICE.getType());
            userInfoAuthVO.setVoiceList(voiceFiles);
            //添加视频信息
            List<UserInfoAuthFile> videoFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(), FileTypeEnum.VIDEO.getType());
            userInfoAuthVO.setVideoList(videoFiles);
        }
    }

    /**
     * 从副本表查询用户认证信息
     *
     * @param userInfoAuthVO
     */
    private boolean findUserAuthInfoByTemp(UserInfoAuthVO userInfoAuthVO) {
        Integer userId = userInfoAuthVO.getUserId();
        if (userId == null) {
            return false;
        }

        //主图
        String mainPic;
        UserInfoAuthFileTempVO tempVO = new UserInfoAuthFileTempVO();
        tempVO.setUserId(userInfoAuthVO.getUserId());
        tempVO.setType(FileTypeEnum.MAIN_PIC.getType());
        List<UserInfoAuthFileTemp> tempList = userInfoAuthFileTempDao.findByParameter(tempVO);
        if (CollectionUtil.isNotEmpty(tempList)) {
            mainPic = tempList.get(0).getUrl();
            userInfoAuthVO.setMainPicUrl(mainPic);
        } else {
            return false;
        }

        //写真图
        tempVO.setType(FileTypeEnum.PIC.getType());
        List<UserInfoAuthFileTemp> portraitFilesList = userInfoAuthFileTempDao.findByParameter(tempVO);
        if (CollectionUtil.isNotEmpty(portraitFilesList)) {
            List<UserInfoAuthFile> fileList = new ArrayList<>();
            for (UserInfoAuthFileTemp fileTemp : portraitFilesList) {
                UserInfoAuthFile file = new UserInfoAuthFile();
                BeanUtil.copyProperties(fileTemp, file);
                fileList.add(file);
            }
            userInfoAuthVO.setPortraitList(fileList);
        } else {
            return false;
        }

        //声音
        tempVO.setType(FileTypeEnum.VOICE.getType());
        List<UserInfoAuthFileTemp> voiceTempList = userInfoAuthFileTempDao.findByParameter(tempVO);
        List<UserInfoAuthFile> voiceList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(voiceTempList)) {
            UserInfoAuthFileTemp voiceTemp = voiceTempList.get(0);
            UserInfoAuthFile voiceFile = new UserInfoAuthFile();
            BeanUtil.copyProperties(voiceTemp, voiceFile);
            voiceList.add(voiceFile);
        } else {
            return false;
        }
        userInfoAuthVO.setVoiceList(voiceList);
        return true;
    }


    /**
     * 查询用户信息所有标签
     *
     * @param userId
     * @return
     */
    public List<TagVO> findAllUserTagSelected(int userId, Boolean ignoreNotUser) {
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
     * 更新陪玩师声音文件
     *
     * @param fileTemp       声音文件bean
     * @param userInfoAuthId 陪玩师认证id
     */
    private void createAndActivateUserAuthVoice(UserInfoAuthFileTemp fileTemp, Integer userInfoAuthId) {
        String voiceUrl = fileTemp.getUrl();
        if (StringUtils.isBlank(voiceUrl)) {
            return;
        }

        List<UserInfoAuthFile> voiceList = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthId,
                FileTypeEnum.VOICE.getType());
        if (CollectionUtil.isEmpty(voiceList)) {
            UserInfoAuthFile createFile = new UserInfoAuthFile();
            BeanUtil.copyProperties(fileTemp, createFile);
            createFile.setInfoAuthId(userInfoAuthId);
            userInfoAuthFileService.create(createFile);
            return;
        }

        String dbVoiceUrl = voiceList.get(0).getUrl();
        if (dbVoiceUrl.equals(voiceUrl)) {
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
     * 更新陪玩师写真图集
     *
     * @param portraitUrlList 写真图集列表
     * @param userInfoAuthId  陪玩师认证id
     */
    private void createAndActivateUserAuthPortrait(List<String> portraitUrlList, Integer userInfoAuthId) {
        if (CollectionUtil.isEmpty(portraitUrlList)) {
            return;
        }
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
     *
     * @param mainPicUrl
     * @param userId
     */
    public void createUserMainPic(String mainPicUrl, Integer userId) {
        if (StringUtils.isBlank(mainPicUrl)) {
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
     *
     * @param portraitUrls
     * @param userId
     */
    public void createUserAuthPortrait(String[] portraitUrls, Integer userId) {
        if (portraitUrls == null || portraitUrls.length == 0) {
            return;
        }
        List<String> portraitUrlList = Arrays.asList(portraitUrls);
        for (int i = 0; i < portraitUrlList.size(); i++) {
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

    @Override
    public void setSubstitute(int id, Integer substituteId) {

        UserInfoAuth uia = new UserInfoAuth();
        uia.setUserId(id);
        uia.setUpdateTime(new Date());
        uia.setImSubstituteId(substituteId);

        userInfoAuthDao.updateByUserId(uia);
    }


    @Override
    public List<UserInfoAuthVO> getAutoSayHelloUser() {

        //取出自动问好陪玩师信息
        List<UserInfoAuthVO> uav = userInfoAuthDao.getAutoSayHelloUser();

        return uav;
    }


    @Override
    public void setUserAgentImStatus(boolean agentStatus, User userInfo) {

        if (!agentStatus) {
            //判断用户24小时内是否可将开关关闭
            String openStr = redisOpenService.get(RedisKeyEnum.USER_AGENT_IM_OPEN.generateKey(userInfo.getId()));
            if (StringUtils.isNotBlank(openStr)) {
                throw new UserAuthException(UserAuthException.ExceptionCode.USER_AGENT_IM_CD);
            }
        } else {
            //保存开关CD  24小时
            redisOpenService.set(RedisKeyEnum.USER_AGENT_IM_OPEN.generateKey(userInfo.getId()), "true", Constant.ONE_DAY);
            //发送邮件
            MailUtil.sendMail(configProperties.getOrdermail().getAddress(), configProperties.getOrdermail().getPassword(), "陪玩师申请开通代聊服务", userInfo.getNickname() + "申请开通代聊服务，ID：" + userInfo.getId() + "，手机号：" + userInfo.getMobile() + "，请与之联系获取私照", new String[]{configProperties.getOrdermail().getTargetAddress()});
        }


        UserInfoAuth u = new UserInfoAuth();
        u.setOpenSubstituteIm(agentStatus);
        u.setUserId(userInfo.getId());

        this.updateByUserId(u);
    }

    /**
     * 修改用户接单数和允许最大定价价格
     *
     * @param order
     */
    @Override
    public void updateOrderCountAndMaxPrice(Order order) {
        if(order == null){
            log.info("修改用户接单数时，订单为空");
            return ;
        }
        UserInfoAuth userInfoAuth = findByUserId(order.getUserId());
        if(userInfoAuth == null){
            log.info("修改用户接单数时，未查询到用户id为{}的用户认证信息", order.getUserId());
            return ;
        }
        Integer orderCount = userInfoAuth.getOrderCount() != null ? userInfoAuth.getOrderCount() + 1 : 1;
        //修改用户接单数
        userInfoAuth.setOrderCount(orderCount);
        update(userInfoAuth);
        //
        UserTechAuth userTechAuth = userTechAuthService.findTechByCategoryAndUser(order.getCategoryId(),userInfoAuth.getUserId());
        if(userTechAuth == null){
            log.info("修改用户技能接单数和允许最大定价价格时，未查询到技能，分类id为{},用户id为{}", order.getCategoryId(),order.getUserId());
            return ;
        }
        Integer techOrderCount = userTechAuth.getOrderCount() != null ? userTechAuth.getOrderCount() + 1 : 1;
        userTechAuth.setOrderCount(techOrderCount);
        PriceRuleVO priceRuleVO = new PriceRuleVO();
        priceRuleVO.setCategoryId(order.getCategoryId());
        priceRuleVO.setOrderCount(techOrderCount);
        PriceRule priceRule = priceRuleService.findMaxPrice(priceRuleVO);
        if(priceRule != null && priceRule.getPrice() != null){
            userTechAuth.setMaxPrice(priceRule.getPrice());
        }
        userTechAuthService.update(userTechAuth);
        log.info("修改用户技能接单数和允许最大定价价格时，未查询到技能，分类id为{},用户id为{}", order.getCategoryId(),order.getUserId());
    }
}
