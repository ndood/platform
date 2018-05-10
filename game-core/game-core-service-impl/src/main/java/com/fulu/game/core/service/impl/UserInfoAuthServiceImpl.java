package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.AuthStatusEnum;
import com.fulu.game.common.enums.FileTypeEnum;
import com.fulu.game.common.enums.UserInfoFileTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.enums.exception.UserExceptionEnums;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.fulu.game.core.dao.UserInfoAuthDao;


@Service
public class UserInfoAuthServiceImpl extends AbsCommonService<UserInfoAuth,Integer> implements UserInfoAuthService {

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
    @Autowired
    private UserTechAuthService utaService;

    @Override
    public ICommonDao<UserInfoAuth, Integer> getDao() {
        return userInfoAuthDao;
    }

    public UserInfoAuth findByUserId(Integer userId){
        UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
        userInfoAuthVO.setUserId(userId);
        UserInfoAuth userInfoAuth = userInfoAuthDao.findByParameter(userInfoAuthVO);
        return userInfoAuth;
    }

    @Override
    public UserInfoAuthVO save(UserInfoAuthVO userInfoAuthVO) {
        //更新用户信息
        User user = userService.findById(userInfoAuthVO.getUserId());
        user.setHeadPortraitsUrl(userInfoAuthVO.getHeadUrl());
        user.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        user.setIdcard(userInfoAuthVO.getIdCard());
        user.setRealname(userInfoAuthVO.getRealname());
        user.setGender(userInfoAuthVO.getGender());
        user.setUserInfoAuth(AuthStatusEnum.VERIFIED.getType());
        userService.update(user);
        //忽略为null的属性
        BeanUtil.CopyOptions copyOptions = BeanUtil.CopyOptions.create();
        copyOptions.setIgnoreNullValue(true);
        //添加认证信息
        UserInfoAuth userInfoAuth = new UserInfoAuth();
        BeanUtil.copyProperties(userInfoAuthVO,userInfoAuth,copyOptions);
        userInfoAuth.setUpdateTime(new Date());
        if(userInfoAuth.getId()==null){
            userInfoAuth.setCreateTime(new Date());
            create(userInfoAuth);
        }else{
            update(userInfoAuth);
        }
        //添加认证身份证文件
        createUserIdCard(user.getId(),userInfoAuthVO.getIdCardHeadUrl(),userInfoAuthVO.getIdCardEmblemUrl(),userInfoAuthVO.getIdCardHandUrl());
        //添加用户认证写真图片
        createUserAuthPortrait(userInfoAuthVO.getPortraitUrls(),userInfoAuth.getId());
        //添加语音介绍
        createUserAuthVoice(userInfoAuthVO.getVoiceUrl(),userInfoAuth.getId());
        //添加用户信息标签
        createUserInfoTags(userInfoAuthVO.getTags(),user.getId());
        return userInfoAuthVO;
    }



    @Override
    public UserInfoAuthVO findUserAuthInfoByUserId(Integer userId) {
        User user = userService.findById(userId);
        UserInfoAuth userInfoAuth = findByUserId(userId);
        if(userInfoAuth==null){
            userInfoAuth =  new UserInfoAuth();
            userInfoAuth.setUserId(userId);
        }
        UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
        BeanUtil.copyProperties(userInfoAuth,userInfoAuthVO);
        copyUserInfo2InfoAuthVo(user,userInfoAuthVO);
        userInfoAuthVO.setRealMobile(user.getMobile());
        //查询身份证信息
        List<UserInfoFile> userInfoFileList=  userInfoFileService.findByUserId(userId);
        userInfoAuthVO.setIdCardList(userInfoFileList);
        //查询写真信息和声音
        copyAuthFile2InfoAuthVo(userInfoAuthVO);
        List<TagVO> allPersonTagVos = findAllUserTag(userId,false);
        userInfoAuthVO.setGroupTags(allPersonTagVos);
        return userInfoAuthVO;
    }

    @Override
    public UserInfoVO findUserCardByUserId(Integer userId,Boolean hasPhotos,Boolean hasVoice,Boolean hasTags,Boolean hasTechs){
        User user = userService.findById(userId);
        if(null == user){
            throw new UserException(UserExceptionEnums.USER_NOT_EXIST_EXCEPTION);
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
        if (null != userInfoAuth){
            userInfo.setMainPhotoUrl(userInfoAuth.getMainPicUrl());
            //查询用户写真图
            if(hasPhotos){
                List<String> photos = new ArrayList<>();
                photos.add(userInfoAuth.getMainPicUrl());
                List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuth.getId(),FileTypeEnum.PIC.getType());
                for(UserInfoAuthFile authFile : portraitFiles){
                    photos.add(authFile.getUrl());
                }
                userInfo.setPhotos(photos);
            }
            //查询用户声音
            if(hasVoice){
                List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuth.getId(),FileTypeEnum.VOICE.getType());
                for(UserInfoAuthFile authFile : voiceFiles){
                    userInfo.setVoice(authFile.getUrl());
                }
            }
        }


        //查询用户标签
        if (hasTags){
            List<PersonTag> personTagList = personTagService.findByUserId(userId);
            List<String> tags = new ArrayList<>();
            for(PersonTag personTag : personTagList){
                tags.add(personTag.getName());
            }
            userInfo.setTags(tags);
        }
        //查询用户技能
        if (hasTechs){
            List<UserTechAuth> userTechAuthList = utaService.findByUserId(userId,true);
            List<String> techList = new ArrayList<String>();
            for (UserTechAuth userTechAuth:userTechAuthList) {
                techList.add(userTechAuth.getCategoryName());
            }
            userInfo.setTechs(techList);
        }
        return userInfo;
    }




    @Override
    public PageInfo<UserInfoAuthVO> list(Integer pageNum, Integer pageSize, String orderBy) {
        if(StringUtils.isBlank(orderBy)){
            orderBy = "update_time desc";
        }
        List<UserInfoAuthVO> userInfoAuthVOList = new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<UserInfoAuth> userInfoAuths =  findAll();
        for(UserInfoAuth userInfoAuth : userInfoAuths){
            UserInfoAuthVO userInfoAuthVO = new UserInfoAuthVO();
            BeanUtil.copyProperties(userInfoAuth,userInfoAuthVO);
            User user = userService.findById(userInfoAuthVO.getUserId());
            copyUserInfo2InfoAuthVo(user,userInfoAuthVO);
            //查询身份证信息
            List<UserInfoFile> userInfoFileList=  userInfoFileService.findByUserId(user.getId());
            userInfoAuthVO.setIdCardList(userInfoFileList);
            //查询写真信息和声音
            copyAuthFile2InfoAuthVo(userInfoAuthVO);
            List<TagVO> allPersonTagVos = findAllUserTag(user.getId(),true);
            userInfoAuthVO.setGroupTags(allPersonTagVos);
            userInfoAuthVOList.add(userInfoAuthVO);
        }
        PageInfo page = new PageInfo(userInfoAuths);
        page.setList(userInfoAuthVOList);
        return page;
    }

    private void copyAuthFile2InfoAuthVo(UserInfoAuthVO userInfoAuthVO){
        if(userInfoAuthVO.getId()!=null){
            List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(),FileTypeEnum.PIC.getType());
            userInfoAuthVO.setPortraitList(portraitFiles);
            List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(),FileTypeEnum.VOICE.getType());
            userInfoAuthVO.setVoiceList(voiceFiles);
        }
    }


    private void copyUserInfo2InfoAuthVo(User user, UserInfoAuthVO userInfoAuthVO){
        userInfoAuthVO.setHeadUrl(user.getHeadPortraitsUrl());
        userInfoAuthVO.setIdCard(user.getIdcard());
        userInfoAuthVO.setGender(user.getGender());
        userInfoAuthVO.setRealname(user.getRealname());
        userInfoAuthVO.setAge(user.getAge());
        userInfoAuthVO.setNickname(user.getNickname());
    }

    /**
     * 查询用户信息所有标签
     * @param userId
     * @return
     */
    private List<TagVO> findAllUserTag(Integer userId,Boolean ignoreNotUser){
        List<Tag> allPersonTags = tagService.findAllPersonTags();
        List<TagVO> tagVOList = new ArrayList<>();
        Map<Integer,TagVO> tagVOMap = new HashMap<>();
        for(Tag tag:allPersonTags){
            if(ObjectUtil.equal(0,tag.getPid())){
                TagVO groupTag = new TagVO();
                BeanUtil.copyProperties(tag,groupTag);
                tagVOMap.put(tag.getId(),groupTag);
                tagVOList.add(groupTag);
            }else{
                if(tagVOMap.containsKey(tag.getPid())){
                    TagVO sonTag = new TagVO();
                    BeanUtil.copyProperties(tag,sonTag);
                    if(isUserTag(userId,tag)){
                        sonTag.setSelected(true);
                    }
                    if(ignoreNotUser){
                        if(sonTag.isSelected()){
                            tagVOMap.get(tag.getPid()).getSonTags().add(sonTag);
                        }
                    }else{
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
    private Boolean isUserTag(Integer userId,Tag tag){
        List<PersonTag> userPersonTagList =  personTagService.findByUserId(userId);
        for(PersonTag personTag : userPersonTagList){
            if(personTag.getTagId().equals(tag.getId())){
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
    public void  createUserIdCard(Integer userId,String idCardHeadUrl,String idCardEmblemUrl,String idCardHandUrl){
        if(idCardHeadUrl!=null){
            createUserInfoFile(userId,idCardHeadUrl,UserInfoFileTypeEnum.IDCARD_HEAD.getMsg(), UserInfoFileTypeEnum.IDCARD_HEAD.getType());
        }
        if(idCardEmblemUrl!=null){
            createUserInfoFile(userId,idCardEmblemUrl,UserInfoFileTypeEnum.IDCARD_EMBLEM.getMsg(), UserInfoFileTypeEnum.IDCARD_EMBLEM.getType());
        }
        if(idCardHandUrl!=null){
            createUserInfoFile(userId,idCardHandUrl,UserInfoFileTypeEnum.IDCARD_HAND.getMsg(), UserInfoFileTypeEnum.IDCARD_HAND.getType());
        }
    }


    private void createUserInfoFile(Integer userId,String fileUrl,String name,Integer type){
        UserInfoFile userInfoFile = new UserInfoFile();
        userInfoFile.setName(name);
        userInfoFile.setUrl(fileUrl);
        userInfoFile.setUserId(userId);
        userInfoFile.setCreateTime(new Date());
        userInfoFile.setType(type);
        userInfoFileService.create(userInfoFile);
    }

    /**
     * 添加用户写真图集
     * @param portraitUrls
     * @param userInfoAuthId
     */
    public void createUserAuthPortrait(String[] portraitUrls,Integer userInfoAuthId){
        if(portraitUrls==null){
            return;
        }
        for(int i=0;i<portraitUrls.length;i++){
            UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
            String portraitUrl = portraitUrls[i];
            userInfoAuthFile.setUrl(portraitUrl);
            userInfoAuthFile.setInfoAuthId(userInfoAuthId);
            userInfoAuthFile.setName("写真"+(i+1));
            userInfoAuthFile.setCreateTime(new Date());
            userInfoAuthFile.setType(FileTypeEnum.PIC.getType());
            userInfoAuthFileService.create(userInfoAuthFile);
        }
    }

    /**
     * 添加用户认证声音
     * @param voiceUrl
     * @param userInfoAuthId
     */
    public void createUserAuthVoice(String voiceUrl,Integer userInfoAuthId){
        if(voiceUrl==null){
            return;
        }
        userInfoAuthFileService.deleteByUserAuthIdAndType(userInfoAuthId,FileTypeEnum.VOICE.getType());
        UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
        userInfoAuthFile.setUrl(voiceUrl);
        userInfoAuthFile.setInfoAuthId(userInfoAuthId);
        userInfoAuthFile.setName("语音介绍");
        userInfoAuthFile.setCreateTime(new Date());
        userInfoAuthFile.setType(FileTypeEnum.VOICE.getType());
        userInfoAuthFileService.create(userInfoAuthFile);
    }

    /**
     * 添加用户标签组
     * @param tags
     * @param userId
     */
    public void createUserInfoTags(Integer[] tags,Integer userId){
        if(tags==null){
            return;
        }
        if(tags.length>0){
            personTagService.deleteByUserId(userId);
        }
        for(Integer tagId : tags){
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
