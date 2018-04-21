package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.AuthStatusEnum;
import com.fulu.game.common.enums.FileTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.service.*;
import com.xiaoleilu.hutool.util.BeanUtil;
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
    public UserInfoAuthVO create(UserInfoAuthVO userInfoAuthVO) {
        //更新用户信息
        User user = userService.findById(userInfoAuthVO.getUserId());
        user.setHeadPortraitsUrl(userInfoAuthVO.getHeadUrl());
        user.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        user.setIdcard(userInfoAuthVO.getIdCard());
        user.setRealname(userInfoAuthVO.getRealname());
        user.setUserInfoAuth(AuthStatusEnum.VERIFIED.getType());
        userService.update(user);
        //忽略为null的属性
        BeanUtil.CopyOptions copyOptions = BeanUtil.CopyOptions.create();
        copyOptions.setIgnoreNullValue(true);
        //添加认证信息
        UserInfoAuth userInfoAuth = new UserInfoAuth();
        BeanUtil.copyProperties(userInfoAuthVO,userInfoAuth,copyOptions);
        userInfoAuth.setCreateTime(new Date());
        userInfoAuth.setUpdateTime(new Date());
        create(userInfoAuth);
        //添加认证身份证文件
        Map<String,String> userInfoMap = new HashMap<>();
        userInfoMap.put("身份证人像面",userInfoAuthVO.getIdCardHeadUrl());
        userInfoMap.put("身份证国徽面",userInfoAuthVO.getIdCardEmblemUrl());
        userInfoMap.put("手持身份证照片",userInfoAuthVO.getIdCardHandUrl());
        createUserInfoFile(userInfoMap,user.getId());
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
        userInfoAuthVO.setHeadUrl(user.getHeadPortraitsUrl());
        userInfoAuthVO.setIdCard(user.getIdcard());
        userInfoAuthVO.setGender(user.getGender());
        userInfoAuthVO.setRealname(user.getRealname());

        //查询身份证信息
        List<UserInfoFile> userInfoFileList=  userInfoFileService.findByUserId(userId);
        userInfoAuthVO.setIdCardList(userInfoFileList);
        //查询写真信息和声音
        if(userInfoAuthVO.getId()!=null){
            List<UserInfoAuthFile> portraitFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(),FileTypeEnum.PIC.getType());
            userInfoAuthVO.setPortraitList(portraitFiles);
            List<UserInfoAuthFile> voiceFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuthVO.getId(),FileTypeEnum.VOICE.getType());
            userInfoAuthVO.setVoiceList(voiceFiles);
        }

        //查询所有标签组
        List<Tag> allPersonTags = tagService.findAllPersonTags();

        //查询用户个人标签
        List<PersonTag> userPersonTagList =  personTagService.findByUserId(userId);
        List<TagVO> allPersonTagVos = new ArrayList<>();
        if(userPersonTagList!=null){
            for(Tag tag : allPersonTags){
                TagVO tagVO = new TagVO();
                BeanUtil.copyProperties(tag,tagVO);
                for(PersonTag personTag :userPersonTagList){
                    if(personTag.getTagId().equals(tagVO.getId())){
                        tagVO.setSelected(true);
                    }
                }
                allPersonTagVos.add(tagVO);
            }
        }
        userInfoAuthVO.setGroupTags(allPersonTagVos);
        return userInfoAuthVO;
    }

    /**
     * 添加用户身份文件
     * @param userInfoMap
     * @param userId
     */
    public void  createUserInfoFile(Map<String,String> userInfoMap,Integer userId){
        userInfoMap.forEach((k,v)->{
            UserInfoFile userInfoFile = new UserInfoFile();
            userInfoFile.setName(k);
            userInfoFile.setUrl(v);
            userInfoFile.setUserId(userId);
            userInfoFile.setCreateTime(new Date());
            userInfoFile.setType(FileTypeEnum.PIC.getType());
            userInfoFileService.create(userInfoFile);
        });
    }

    /**
     * 添加用户写真图集
     * @param portraitUrls
     * @param userInfoAuthId
     */
    public void createUserAuthPortrait(String[] portraitUrls,Integer userInfoAuthId){
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
