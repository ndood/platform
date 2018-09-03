package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.exception.CommonException;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.common.utils.geo.GeoHashUtil;
import com.fulu.game.common.utils.geo.Point;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.DynamicFileVO;
import com.fulu.game.core.entity.vo.DynamicVO;
import com.fulu.game.core.search.component.DynamicSearchComponent;
import com.fulu.game.core.search.domain.DynamicDoc;
import com.fulu.game.core.search.domain.DynamicFileDoc;
import com.fulu.game.core.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.DynamicDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("dynamicService")
public class DynamicServiceImpl extends AbsCommonService<Dynamic,Long> implements DynamicService {

    @Autowired
	private DynamicDao dynamicDao;

    @Autowired
    private UserService userService;

    @Autowired
    private DynamicFileService dynamicFileService;

    @Autowired
    private DynamicSearchComponent dynamicSearchComponent;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserTechInfoService userTechInfoService;

    @Autowired
    private UserFriendService userFriendService;


    @Override
    public ICommonDao<Dynamic, Long> getDao() {
        return dynamicDao;
    }

    /**
     * 保存动态接口
     *  1、先保存MySQL数据库，保存成功之后保存ES
     * @param dynamicVO
     * @return
     */
    @Override
    public Dynamic save(DynamicVO dynamicVO) {
        if(dynamicVO == null){
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        User user = userService.getCurrentUser();
        dynamicVO.setUserId(user.getId());
        ClientInfo clientInfo = SubjectUtil.getUserClientInfo();
        if(clientInfo != null ){
            dynamicVO.setCityCode(clientInfo.get_ipCity());
            dynamicVO.setCityName(clientInfo.get_cityName());
        }
        if(dynamicVO.getCreateTime() == null){
            dynamicVO.setCreateTime(new Date());
            dynamicVO.setUpdateTime(new Date());
        }
        if(dynamicVO.getLon() != null && dynamicVO.getLat() != null){
            Point point = new Point(dynamicVO.getLon(),dynamicVO.getLat());
            String geoHash = GeoHashUtil.encode(point);
            dynamicVO.setGeohash(geoHash);
            if(geoHash != null && geoHash.length() > 2){
                dynamicVO.setGeohashShort(geoHash.substring(0,geoHash.length() - 2));
            }
        }
        dynamicVO.setStatus(1);
        saveDynamic(dynamicVO);
        saveDynamicFiles(dynamicVO);
        saveDynamicES(dynamicVO, user);
        return null;
    }

    /**
     *获取动态列表接口
     * @param pageSize 每页数量
     * @param slide 0：下滑刷新；1：上划加载更多
     * @param id 上划：传客户端最大id；下滑：传客户端最小id
     * @param type 动态页tab类型（1：精选；2：关注）
     * @return
     */
    @Override
    public Page<DynamicDoc> list(Integer pageSize, Integer slide, Integer id, Integer type) {
        List<String> userIdList = null;
        User user = userService.getCurrentUser();
        if(type != null && type == 2){
            userIdList = new ArrayList<>();
            userIdList.add(user.getId() + "");
            List<UserFriend> list = userFriendService.getAllAttentionsByUserId(user.getId());
            if(list != null && !list.isEmpty() && list.size() > 0){
                for(UserFriend friend : list){
                    userIdList.add(friend.getToUserId() + "");
                }
            }
        }
        return getDynamicList( pageSize, slide, id, userIdList, user);
    }



    /**
     * 获取用户动态列表接口
     * @param pageSize 每页数量
     * @param slide    0：下滑刷新；1：上划加载更多
     * @param id       上划：传客户端最大id；下滑：传客户端最小id
     * @param userId   非必传，不传查用户自己动态，传了查其他用户动态
     * @return
     */
    @Override
    public Page<DynamicDoc> userDynamicList(Integer pageSize, Integer slide, Integer id, Integer userId) {
        List<String> userIdList = new ArrayList<>();
        User user = userService.getCurrentUser();
        if(userId == null){
            userIdList.add(user.getId() + "");
        } else {
            userIdList.add(userId + "");
        }
        return getDynamicList( pageSize, slide, id, userIdList, user);
    }


    /**
     * 获取动态详情
     * @param id
     * @return
     */
    @Override
    public DynamicDoc getDynamicDocById(Long id) {
        return dynamicSearchComponent.searchById(id, DynamicDoc.class);
    }

    /**
     * 获取用户最新动态，"我的"里面使用
     *
     * @param userId
     * @return
     */
    @Override
    public List<DynamicVO> getNewestDynamicList(Integer userId) {
        List<DynamicVO> result = new ArrayList<>();
        Page<DynamicDoc> page = userDynamicList(4, 0, 0, userId);
        if(page != null){
            List<DynamicDoc> list = page.getResult();
            if(list != null && !list.isEmpty()){
                for(DynamicDoc doc: list){
                    DynamicVO dynamicVO = new DynamicVO();
                    dynamicVO.setId(doc.getId());
                    dynamicVO.setContent(doc.getContent());
                    dynamicVO.setType(doc.getType());
                    dynamicVO.setCreateTime(doc.getCreateTime());
                    List<DynamicFile> fileList = new ArrayList<>();
                    if(doc.getFiles() != null){
                        List<DynamicFileDoc> fileDocList = doc.getFiles();
                        for(DynamicFileDoc fileDoc: fileDocList){
                            DynamicFile dynamicFile = new DynamicFile();
                            dynamicFile.setUrl(fileDoc.getUrl());
                            fileList.add(dynamicFile);
                        }
                    }
                    dynamicVO.setDynamicFiles(fileList);
                    result.add(dynamicVO);
                }
            }
        }
        return result;
    }

    /**
     * 通过ID删除动态信息
     * @param id
     * @return
     */
    @Override
    public int deleteDynamicById(Long id){
        Dynamic dynamic = findById(id);
        // 记录不存在
        if(dynamic == null){
            throw new CommonException(CommonException.ExceptionCode.RECORD_NOT_EXSISTS);
        }
        User user = userService.getCurrentUser();
        // 用户不匹配
        if(user.getId().intValue() != dynamic.getUserId().intValue()){
            throw new UserException(UserException.ExceptionCode.USER_MISMATCH_EXCEPTION);
        }
        deleteDynamicEsById(id);
        return deleteById(id);
    }

    /**
     * 修改动态中实时变化的值
     *
     * @param id       动态id
     * @param rewards  是否自增打赏次数（true：自增；false：不自增）
     * @param likes    点赞次数 (取消点赞： -1；点赞：1)
     * @param comments 评论次数 (删除评论： -1；评论：1)
     * @param clicks   是否自增点击次数（true：自增；false：不自增）
     * @return
     */
    @Override
    public boolean updateIndexFilesById(Long id, boolean rewards, Integer likes, Integer comments, boolean clicks) {
        Integer userId = 0;
        Dynamic dynamic = findById(id);
        if(dynamic == null){
            log.info("修改动态索引异常，未找到id： {}", id);
            return false;
        }
        if(rewards ){
            if(dynamic.getRewards() != null){
                dynamic.setRewards(dynamic.getRewards() + 1);
            } else {
                dynamic.setRewards( 1L);
            }
        }
        if(likes != null ){
            if(dynamic.getLikes() != null){
                dynamic.setLikes(dynamic.getLikes() + likes);
            } else {
                dynamic.setLikes(1L);
            }
            User user = userService.getCurrentUser();
            userId = user.getId();
        }
        if(comments != null ){
            if(dynamic.getComments() != null){
                dynamic.setComments(dynamic.getComments() + comments);
            } else {
                dynamic.setComments(1L);
            }
        }
        if(clicks ){
            if(dynamic.getClicks() != null){
                dynamic.setClicks(dynamic.getClicks() + 1);
            } else {
                dynamic.setClicks(1L);
            }
        }
        update(dynamic);
        // 修改ES信息
        dynamicSearchComponent.updateIndexFilesById( id, rewards, likes, comments, clicks, userId);
        return true;
    }

    /**
     * 获取动态列表
     * @param pageSize
     * @param slide
     * @param id
     * @param userIdList
     * @param user
     * @return
     */
    private Page<DynamicDoc> getDynamicList(Integer pageSize, Integer slide, Integer id, List<String> userIdList, User user){
        Page<DynamicDoc> page = null;
        try {
            page = dynamicSearchComponent.searchDynamicDocList(slide, id, null, null,pageSize, "",userIdList ,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userIsLike(page, user);
        return page;
    }

    /**
     * 判断用户是否点赞
     * @param pages
     * @param user
     */
    private void userIsLike(Page<DynamicDoc> pages, User user){
        if(pages == null){
            return ;
        }
        int userId = user.getId();
        for(DynamicDoc dynamicDoc: pages){
            BitSet bitSet = dynamicDoc.getLikeUserIds();
            if(bitSet != null && bitSet.get(userId)){
                dynamicDoc.setIsLike(1);
            } else {
                dynamicDoc.setIsLike(0);
            }
        }
    }

    /**
     * 保存动态信息
     * @param dynamicVO
     */
    private void saveDynamic(DynamicVO dynamicVO){
        if(dynamicVO.getIsHot() == null){
            dynamicVO.setIsHot(0);
        }
        if(dynamicVO.getIsTop() == null){
            dynamicVO.setIsTop(0);
        }
        if(dynamicVO.getCreateTime() == null){
            dynamicVO.setCreateTime(DateUtil.date());
            dynamicVO.setUpdateTime(DateUtil.date());
        }
        if(dynamicVO.getStatus() == null){
            dynamicVO.setStatus(1);
        }
        dynamicDao.create(dynamicVO);
    }

    /**
     * 保存动态文件
     * @param dynamicVO
     */
    private void saveDynamicFiles(DynamicVO dynamicVO){
        List<DynamicFile> dynamicFiles = new ArrayList<>();
        if(dynamicVO != null){
            if(dynamicVO.getUrls() != null){
                String[] urls = dynamicVO.getUrls();
                for(String url: urls) {
                    DynamicFileVO dynamicFileVO = new DynamicFileVO();
                    dynamicFileVO.setDynamicId(dynamicVO.getId());
                    dynamicFileVO.setType(dynamicVO.getType());
                    dynamicFileVO.setUrl(url);
                    dynamicFileVO.setCreateTime(DateUtil.date());
                    dynamicFileVO.setUpdateTime(DateUtil.date());
                    dynamicFileVO.setStatus(1);
                    dynamicFileVO.setPlayCount(0L);
                    dynamicFiles.add(dynamicFileVO);
                    dynamicFileService.save(dynamicFileVO);
                }
            }
        }
        dynamicVO.setDynamicFiles(dynamicFiles);
    }

    /**
     * 保存动态ES信息
     * @param dynamicVO
     */
    private void saveDynamicES(DynamicVO dynamicVO, User user){
        DynamicDoc dynamicDoc = new DynamicDoc();
        dynamicDoc.setId(dynamicVO.getId());
        dynamicDoc.setUserId(user.getId());
        dynamicDoc.setUserAge(user.getAge());
        dynamicDoc.setUserGender(user.getGender());
        dynamicDoc.setUserHeadUrl(user.getHeadPortraitsUrl());
        dynamicDoc.setUserNickname(user.getNickname());
        dynamicDoc.setContent(dynamicVO.getContent());
        dynamicDoc.setCityCode(dynamicVO.getCityCode());
        dynamicDoc.setCityName(dynamicVO.getCityName());
        dynamicDoc.setClicks(dynamicVO.getClicks());
        dynamicDoc.setComments(dynamicVO.getComments());
        dynamicDoc.setGeohash(dynamicVO.getGeohash());
        dynamicDoc.setGethashShort(dynamicVO.getGeohashShort());
        dynamicDoc.setType(dynamicVO.getType());
        dynamicDoc.setIsHot(dynamicVO.getIsHot());
        dynamicDoc.setIsTop(dynamicVO.getIsTop());
        dynamicDoc.setCreateTime(dynamicVO.getCreateTime());
        dynamicDoc.setUpdateTime(dynamicVO.getUpdateTime());
        dynamicDoc.setStatus(dynamicVO.getStatus());
        dynamicDoc.setTechInfoId(dynamicVO.getTechInfoId());
        dynamicDoc.setGeohash(dynamicVO.getGeohash());
        dynamicDoc.setGethashShort(dynamicVO.getGeohashShort());
        if(dynamicVO.getTechInfoId() != null && dynamicVO.getTechInfoId() > 0){
            //TODO shijiaoyun 此处需要获取下单技能信息
//            UserTechInfo userTechInfo = userTechInfoService.findById(dynamicVO.getTechInfoId());
//            Product product = productService.findById(dynamicVO.getTechInfoId());
//            dynamicDoc.setTechInfoId(userTechInfo.getTechAttrId());
//            dynamicDoc.setTechInfoName(userTechInfo.get);
        }
        if(dynamicVO.getDynamicFiles() != null){
            List<DynamicFileDoc> list = new ArrayList<>();
            for(DynamicFile file: dynamicVO.getDynamicFiles()) {
                DynamicFileDoc dynamicFileDoc = new DynamicFileDoc();
                dynamicFileDoc.setType(dynamicVO.getType());
                dynamicFileDoc.setUrl(file.getUrl());
                dynamicFileDoc.setPlayCount(file.getPlayCount());
                list.add(dynamicFileDoc);
            }
            dynamicDoc.setFiles(list);
        }
        dynamicSearchComponent.saveDynamicIndex(dynamicDoc);
    }

    /**
     * 删除动态ES信息
     * @param id
     */
    private void deleteDynamicEsById(Long id){
        dynamicSearchComponent.deleteIndex(id);
    }

}
