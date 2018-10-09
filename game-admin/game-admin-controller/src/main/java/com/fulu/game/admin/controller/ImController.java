package com.fulu.game.admin.controller;

import com.alibaba.fastjson.JSON;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.service.impl.PlayMiniAppOrderServiceImpl;
import com.fulu.game.play.service.impl.PlayMiniAppPushServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map.Entry;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/im")
public class ImController extends BaseController {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    
    @Autowired
    private AdminImLogService adminImLogService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private VirtualProductService virtualProductService;

    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;
    
    @Autowired
    private OrderService orderService;

    @Qualifier("playMiniAppOrderServiceImpl")
    @Autowired
    private PlayMiniAppOrderServiceImpl playMiniAppOrderServiceImpl;
    
    @Autowired
    private AdminService adminService;

    
    //减少未读消息数量
    @RequestMapping("/set-count")
    public Result update(@RequestParam(value = "imSubstituteId", required = false) Integer imSubstituteId,
                     @RequestParam(value = "imId", required = false) String imId,
                     @RequestParam(value = "actionCount", required = false) Integer actionCount){


        Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));
        
        if(map != null && map.size() >0){
            
            if(map.get(imId)!=null){

                UserInfoAuthVO temp = JSON.parseObject(map.get(imId).toString(),UserInfoAuthVO.class);

                long currentCount = temp.getUnreadCount().longValue() - actionCount.longValue();
                temp.setUnreadCount(currentCount);
                
                if(currentCount > 0){
                    map.put(imId, JSON.toJSONString(temp));
                }else{
                    map.remove(imId);
                }

                //更新未读消息数
                if(map.size() < 1){
                    redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));
                }else{
                    redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()), map, Constant.ONE_DAY * 3);
                }
                
            }
            
            
        }

        return Result.success().msg("操作成功");
    }

    
    //获取未读消息陪玩师列表
    @RequestMapping("/list")
    public Result list(@RequestParam(value = "imSubstituteId", required = false) Integer imSubstituteId){


        Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));

        List list = new ArrayList();
        if(map!=null && map.size()>0){
            Iterator iter = map.entrySet().iterator(); 
            while (iter.hasNext()) {
                Entry entry = (Entry) iter.next();
                list.add(entry.getValue());
            }
        }
        

        return Result.success().data(list).msg("操作成功");
    }

    //保存admin上的im聊天信息
    @RequestMapping("/save-log")
    public Result save(AdminImLogVO adminImLogVO) {


        adminImLogService.create(adminImLogVO);

        return Result.success().msg("操作成功");
    }


    @PostMapping(value = "collect")
    public Result log(String content){
        log.error("日志收集:{}",content);
        return Result.success();
    }

    /**
     * 用户-根据imId查询聊天对象User
     *
     * @return
     */
    @PostMapping("/im/get")
    public Result getImUser(@RequestParam("imId") String imId,
                            String content) {
        log.info("根据imId获取用户信息:imId:{};content:{}", imId, content);
        if (StringUtils.isEmpty(imId)) {
            throw new UserException(UserException.ExceptionCode.IllEGAL_IMID_EXCEPTION);
        }
        User user = userService.findByImId(imId);
        if (null == user) {
            return Result.error().msg("未查询到该用户或尚未注册IM");
        }
        log.info("根据imId获取用户信息:imId:{};content:{};user:{}", imId, content, user);
        return Result.success().data(user).msg("查询IM用户成功");
    }

    //发送解锁
    @RequestMapping("/send-lock")
    public Result sendLock(Integer userId ,String name, Integer price, Integer type, Integer sort, String[] urls){

        VirtualProduct vp = new VirtualProduct();
        vp.setName(name);
        vp.setPrice(price);
        vp.setType(type);
        vp.setSort(sort);
        if(urls == null){
            vp.setAttachCount(0);
        }else{
            vp.setAttachCount(urls.length);
        }

        vp = virtualProductService.createVirtualProduct(vp,userId,urls);

        return Result.success().data(vp).msg("操作成功");
    }

    @RequestMapping("/push")
    public Result pushWechatMsg(String content,
                                String acceptImId,
                                String imId)throws Exception{
        log.info("推送模板消息imId:{},acceptImId:{},content:{}",imId,acceptImId,content);
        String result = playMiniAppPushService.pushIMWxTemplateMsg(content,acceptImId,imId);
        return Result.success().msg(result);
    }



    //获取该陪玩师与老板的订单
    @RequestMapping("/banner-order/get")
    public Result getBannerOrderList(Integer authUserId , Integer bossUserId){

        List<Order> list = orderService.getBannerOrderList(authUserId,bossUserId);

        return Result.success().data(list).msg("操作成功");
    }


    //搜索陪玩师
    @RequestMapping("/search-auth/list")
    public Result searchAuthUserList(@RequestParam("pageNum") Integer pageNum,
                                     @RequestParam("pageSize") Integer pageSize, 
                                     String searchWord){

        Admin ad = adminService.getCurrentUser();
        
        PageInfo<User> pageInfo = userService.searchByAuthUserInfo(pageNum,pageSize,ad.getId(),searchWord);

        return Result.success().data(pageInfo).msg("操作成功");
    }

    
    //搜索用户
    @RequestMapping("/search-user/list")
    public Result searchUserList(@RequestParam("pageNum") Integer pageNum,
                                 @RequestParam("pageSize") Integer pageSize,
                                 Integer currentAuthUserId ,
                                 String searchWord){

        PageInfo<User> pageInfo = userService.searchByUserInfo(pageNum,pageSize,currentAuthUserId,searchWord);

        return Result.success().data(pageInfo).msg("操作成功");
    }


    /**
     * 陪玩师同意协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/server/consult-appeal")
    public Result consultAppeal(@RequestParam(required = true) String orderNo,
                                Integer orderEventId , Integer userId) {
        Order order = orderService.findByOrderNo(orderNo);
        
        playMiniAppOrderServiceImpl.consultAgreeOrder(order, orderEventId , userId);
        return Result.success().data(orderNo);
    }


    /**
     * 订单事件查询（查询协商和申诉）
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/order/event")
    public Result orderEvent(@RequestParam(required = true) String orderNo,Integer userId) {
        
        User user = userService.findById(userId);
        OrderEventVO orderEventVO = playMiniAppOrderServiceImpl.findOrderEvent(orderNo,userId);
        return Result.success().data(orderEventVO);
        
    }


    /**
     * 陪玩师拒绝协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/server/consult-reject")
    public Result consultReject(@RequestParam(required = true) String orderNo,
                                Integer orderEventId,
                                String remark,
                                @RequestParam(required = false) String[] fileUrl , Integer userId) {
        
        Order order = orderService.findByOrderNo(orderNo);
        playMiniAppOrderServiceImpl.consultRejectOrder(order, orderEventId, remark, fileUrl , userId);
        
        return Result.success().data(orderNo);
    }


    /**
     * 陪玩师接收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/receive")
    public Result serverReceiveOrder(@RequestParam String orderNo,
                                     String version) {

        Order order = orderService.findByOrderNo(orderNo);
        
        playMiniAppOrderServiceImpl.serverReceiveOrder(order);
        
        return Result.success().data(orderNo).msg("接单成功!");
    }


    /**
     * 陪玩师开始服务
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/start-serve")
    public Result startServerOrder(@RequestParam(required = true) String orderNo) {

        Order order = playMiniAppOrderServiceImpl.findByOrderNo(orderNo);
        
        playMiniAppOrderServiceImpl.serverStartServeOrder(order);
        return Result.success().data(orderNo).msg("操作成功!");
    }


    /**
     * 陪玩师取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/cancel")
    public Result serverCancelOrder(@RequestParam(required = true) String orderNo) {

        Order order = playMiniAppOrderServiceImpl.findByOrderNo(orderNo);
        
        OrderVO orderVO = playMiniAppOrderServiceImpl.serverCancelOrder(order);
        return Result.success().data(orderVO).msg("取消订单成功!");
    }


    /**
     * 陪玩师提交验收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/acceptance")
    public Result serverAcceptanceOrder(@RequestParam(required = true) String orderNo,
                                        String remark, Integer userId,
                                        @RequestParam(required = false)String[] fileUrl) {
        Order order = orderService.findByOrderNo(orderNo);
        User user = userService.findById(userId);
        playMiniAppOrderServiceImpl.serverAcceptanceOrder(order, remark, user,fileUrl);
        return Result.success().data(orderNo).msg("提交订单验收成功!");
    }
    
}
