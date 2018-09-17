package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.CommonException;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.HttpUtils;
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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class DynamicServiceImpl extends AbsCommonService<Dynamic,Integer> implements DynamicService {

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
    private UserFriendService userFriendService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    @Override
    public ICommonDao<Dynamic, Integer> getDao() {
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
        if(dynamicVO.getUserId() == null || dynamicVO.getUserId().intValue() < 0){
            User user = userService.getCurrentUser();
            dynamicVO.setUserId(user.getId());
        }
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
        saveDynamicES(dynamicVO);
        redisOpenService.incr(RedisKeyEnum.DYNAMIC_COUNT.generateKey(dynamicVO.getUserId()));
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
        User user = null;
        if(type != null && type == 2){
            userService.getCurrentUser();
            userIdList = new ArrayList<>();
            userIdList.add(user.getId() + "");
            List<UserFriend> list = userFriendService.getAllAttentionsByUserId(user.getId());
            if(list != null && !list.isEmpty() && list.size() > 0){
                for(UserFriend friend : list){
                    userIdList.add(friend.getToUserId() + "");
                }
            }
        }
        return getDynamicList( pageSize, slide, id, userIdList, user, false);
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
    public Page<DynamicDoc> userDynamicList(Integer pageSize, Integer slide, Integer id, Integer userId, boolean isPicOrVideo) {
        List<String> userIdList = new ArrayList<>();
        User user = userService.getCurrentUser();
        if(userId == null){
            userIdList.add(user.getId() + "");
        } else {
            userIdList.add(userId + "");
        }
        return getDynamicList( pageSize, slide, id, userIdList, user, isPicOrVideo);
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
        return userDynamicList( pageSize, slide, id, userId, false);
    }


    /**
     * 获取动态详情
     * @param id
     * @return
     */
    @Override
    public DynamicDoc getDynamicDocById(Integer id) {
        DynamicDoc dynamicDoc = dynamicSearchComponent.searchById(id, DynamicDoc.class);
        User user = userService.getCurrentUser();
        setDynamicDocUserExt(dynamicDoc, user);
        return dynamicDoc;
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
        DynamicVO params = new DynamicVO();
        // 先获取最新的图片和视频动态
        Page<DynamicDoc> page = userDynamicList(4, 0, 0, userId,true);
        if(page != null){
            List<DynamicDoc> list = page.getResult();
            if(list == null || list.isEmpty()){
                //不存在图片和视频动态，则获取最新的一条动态
                page = userDynamicList(1, 0, 0, userId,false);
                if(page != null){
                    list = page.getResult();
                }
            }
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
     * @param verifyUser 是否验证用户信息是否匹配（true：验证；false：不验证）
     * @return
     */
    @Override
    public int deleteDynamicById(Integer id, boolean verifyUser){
        Dynamic dynamic = findById(id);
        // 记录不存在
        if(dynamic == null){
            throw new CommonException(CommonException.ExceptionCode.RECORD_NOT_EXSISTS);
        }
        User user = userService.getCurrentUser();
        // 用户不匹配
        if(verifyUser && user.getId().intValue() != dynamic.getUserId().intValue()){
            throw new UserException(UserException.ExceptionCode.USER_MISMATCH_EXCEPTION);
        }
        deleteDynamicEsById(id);
        redisOpenService.decr(RedisKeyEnum.DYNAMIC_COUNT.generateKey(id));
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
    public boolean updateIndexFilesById(int id, boolean rewards, Integer likes, Integer comments, boolean clicks) {
        Integer userId = 0;
        Dynamic dynamic = findById(id);
        if(dynamic == null){
            log.info("修改动态索引异常，未找到id： {}", id);
            return false;
        }
        if( rewards ){
            if(dynamic.getRewards() != null){
                dynamic.setRewards(dynamic.getRewards() + 1);
            } else {
                dynamic.setRewards( 1);
            }
        }
        if(likes != null && likes.intValue() != 0){
            if(dynamic.getLikes() != null){
                dynamic.setLikes(dynamic.getLikes() + likes);
            } else {
                dynamic.setLikes(1);
            }
            User user = userService.getCurrentUser();
            userId = user.getId();
        }
        if(comments != null && comments.intValue() != 0){
            if(dynamic.getComments() != null){
                dynamic.setComments(dynamic.getComments() + comments);
            } else {
                dynamic.setComments(1);
            }
        }
        if( clicks ){
            if(dynamic.getClicks() != null){
                dynamic.setClicks(dynamic.getClicks() + 1);
            } else {
                dynamic.setClicks(1);
            }
        }
        update(dynamic);
        // 修改ES信息
        dynamicSearchComponent.updateIndexFilesById( id, rewards, likes, comments, clicks, userId);
        return true;
    }

    /**
     * 后端获取动态列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<DynamicVO> adminList(Integer pageNum, Integer pageSize, String keyword, String startTime, String endTime) {
        String orderBy =  "id desc";
        PageHelper.startPage(pageNum,pageSize,orderBy);
        Admin admin = adminService.getCurrentUser();
        DynamicVO dynamicVO = new DynamicVO();
        dynamicVO.setOperatorId(admin.getId());
        dynamicVO.setKeyword(keyword);
        dynamicVO.setStartTime(startTime);
        dynamicVO.setEndTime(endTime);
        dynamicVO.setStatus(1);
        List<DynamicVO> couponGroupList =  dynamicDao.adminList(dynamicVO);
        PageInfo page = new PageInfo(couponGroupList);
        return page;
    }

    /**
     * 删除所有ES中的动态
     */
    @Override
    public void deleteAll() {
        dynamicSearchComponent.deleteIndexAll();
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
    private Page<DynamicDoc> getDynamicList(Integer pageSize, Integer slide, Integer id, List<String> userIdList, User user, boolean isPicOrVideo){
        Page<DynamicDoc> page = null;
        try {
            page = dynamicSearchComponent.searchDynamicDocList(slide, id, null, null,pageSize, "",userIdList ,null, isPicOrVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDynamicDocsUserExt(page, user);
        return page;
    }

    /**
     * 设置多动态的用户扩展信息
     * 包含是否点赞、是否关注等信息
     * @param pages
     * @param user
     */
    private void setDynamicDocsUserExt(Page<DynamicDoc> pages, User user){
        if(pages == null || user == null){
            return ;
        }
        for(DynamicDoc dynamicDoc: pages){
            setDynamicDocUserExt(dynamicDoc,user);
        }
    }

    /**
     * 设置单动态的用户扩展信息
     * 包含是否点赞、是否关注等信息
     * @param dynamicDoc
     * @param user
     */
    private void setDynamicDocUserExt(DynamicDoc dynamicDoc, User user){
        if(dynamicDoc == null || user == null){
            return ;
        }
        int userId = user.getId();
        BitSet bitSet = dynamicDoc.getLikeUserIds();
        if(bitSet != null && bitSet.get(userId)){
            dynamicDoc.setIsLike(1);
        } else {
            dynamicDoc.setIsLike(0);
        }
        Integer isAttention = redisOpenService.getBitSet(RedisKeyEnum.ATTENTION_USERS.generateKey(userId),dynamicDoc.getUserId()) ? 1: 0;
        dynamicDoc.setIsAttention(isAttention);
        // 设置动态文件信息
//        setFileInfo(dynamicDoc);
    }

    /**
     * 设置图片长宽信息
     * @param dynamicDoc
     */
    private void setFileInfo(DynamicDoc dynamicDoc){
        if(dynamicDoc != null && dynamicDoc.getFiles() != null && dynamicDoc.getFiles().size() == 1){
            DynamicFileDoc dynamicFileDoc = dynamicDoc.getFiles().get(0);
            try {
                if(dynamicFileDoc.getType().intValue() == 1 ){
                    //获取图片信息接口
                    String imgUrl = dynamicFileDoc.getUrl() + "?x-oss-process=image/info";
                    String imgInfoStr = HttpUtils.get(imgUrl,null);
                    JSONObject jsonObject = JSONObject.parseObject(imgInfoStr);
                    dynamicFileDoc.setHeight(Integer.parseInt(jsonObject.getJSONObject("ImageHeight").getString("value")));
                    dynamicFileDoc.setWidth(Integer.parseInt(jsonObject.getJSONObject("ImageWidth").getString("value")));
                    List<DynamicFileDoc> list = new ArrayList<>();
                    list.add(dynamicFileDoc);
                    dynamicDoc.setFiles(list);
                }
            }catch (Exception e){
                e.printStackTrace();
                log.warn("获取图片信息发生异常，图片地址：{}，异常信息：{}",dynamicFileDoc.getUrl() + "?x-oss-process=image/info", e.getMessage());
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
            if(dynamicVO.getFiles() != null){
                String files = dynamicVO.getFiles();
                JSONArray fileJsonArr = JSONArray.parseArray(files);
                for(int i = 0; i < fileJsonArr.size(); i++) {
                    JSONObject fileObject = fileJsonArr.getJSONObject(i);
                    DynamicFileVO dynamicFileVO = new DynamicFileVO();
                    dynamicFileVO.setDynamicId(dynamicVO.getId());
                    dynamicFileVO.setType(dynamicVO.getType());
                    dynamicFileVO.setUrl(fileObject.getString("url"));
                    dynamicFileVO.setWidth(fileObject.getInteger("width") == null ? 0:fileObject.getInteger("width"));
                    dynamicFileVO.setHeight(fileObject.getInteger("height") == null ? 0:fileObject.getInteger("height"));
                    dynamicFileVO.setDuration(fileObject.getInteger("duration") == null ? 0:fileObject.getInteger("duration"));
                    dynamicFileVO.setCreateTime(DateUtil.date());
                    dynamicFileVO.setUpdateTime(DateUtil.date());
                    dynamicFileVO.setStatus(1);
                    dynamicFileVO.setPlayCount(0);
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
    private void saveDynamicES(DynamicVO dynamicVO){
        User user = userService.findById(dynamicVO.getUserId());
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
        dynamicDoc.setLikes(dynamicVO.getLikes());
        dynamicDoc.setRewards(dynamicVO.getRewards());
        dynamicDoc.setGeohash(dynamicVO.getGeohash());
        dynamicDoc.setGethashShort(dynamicVO.getGeohashShort());
        dynamicDoc.setType(dynamicVO.getType());
        dynamicDoc.setIsHot(dynamicVO.getIsHot());
        dynamicDoc.setIsTop(dynamicVO.getIsTop());
        dynamicDoc.setCreateTime(dynamicVO.getCreateTime());
        dynamicDoc.setUpdateTime(dynamicVO.getUpdateTime());
        dynamicDoc.setStatus(dynamicVO.getStatus());
        dynamicDoc.setProductId(dynamicVO.getProductId());
        dynamicDoc.setGeohash(dynamicVO.getGeohash());
        dynamicDoc.setGethashShort(dynamicVO.getGeohashShort());
        if(dynamicVO.getProductId() != null && dynamicVO.getProductId() > 0){
            //获取商品信息、设置动态下单商品信息
            Product product = productService.findById(dynamicVO.getProductId());
            dynamicDoc.setProductName(product.getProductName());
            dynamicDoc.setProductPrice(product.getPrice());
            dynamicDoc.setProductUnit(product.getUnit());
        }
        if(dynamicVO.getDynamicFiles() != null){
            List<DynamicFileDoc> list = new ArrayList<>();
            for(DynamicFile file: dynamicVO.getDynamicFiles()) {
                DynamicFileDoc dynamicFileDoc = new DynamicFileDoc();
                dynamicFileDoc.setType(dynamicVO.getType());
                dynamicFileDoc.setUrl(file.getUrl());
                dynamicFileDoc.setPlayCount(file.getPlayCount());
                dynamicFileDoc.setWidth(file.getWidth());
                dynamicFileDoc.setHeight(file.getHeight());
                dynamicFileDoc.setDuration(file.getDuration());
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
    private void deleteDynamicEsById(Integer id){
        dynamicSearchComponent.deleteIndex(id);
    }

}
