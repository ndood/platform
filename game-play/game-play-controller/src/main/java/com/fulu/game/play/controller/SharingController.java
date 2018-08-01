package com.fulu.game.play.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.entity.vo.UserWechatGroupShareVO;
import com.fulu.game.core.service.SharingService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserWechatGroupShareService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.utils.RequestUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/sharing")
public class SharingController extends BaseController {

    private final SharingService sharingService;
    private final UserWechatGroupShareService userWechatGroupShareService;
    private final UserService userService;
    private final RedisOpenServiceImpl redisOpenService;

    @Autowired
    public SharingController(SharingService sharingService,
                             UserWechatGroupShareService userWechatGroupShareService,
                             UserService userService,
                             RedisOpenServiceImpl redisOpenService) {
        this.sharingService = sharingService;
        this.userWechatGroupShareService = userWechatGroupShareService;
        this.userService = userService;
        this.redisOpenService = redisOpenService;
    }

    @PostMapping("/get")
    public Result get(@RequestParam("shareType") Integer shareType,
                      @RequestParam("gender") Integer gender) {
        SharingVO queryVO = new SharingVO();
        queryVO.setShareType(shareType);
        queryVO.setGender(gender);
        queryVO.setStatus(true);
        List<Sharing> sharingList = sharingService.findByParam(queryVO);
        if (!CollectionUtil.isEmpty(sharingList)) {
            return Result.success().data(sharingList.get(0)).msg("查询成功！");
        } else {
            return Result.error().msg("文案尚未配置或文案弃用！");
        }
    }

    /**
     * 获取用户分享到微信群的分享状态
     *
     * @return 封装结果集
     */
    @RequestMapping("/wechat-group/status")
    @ResponseBody
    public Result getUserShareStatus() {
        User user = userService.getCurrentUser();
        UserWechatGroupShareVO groupShareVO = userWechatGroupShareService.getUserShareStatus(user);
        return Result.success().data(groupShareVO).msg("查询分享状态成功！");
    }

    /**
     * 分享到微信群
     *
     * @param encryptedData 包括敏感数据在内的完整转发信息的加密数据
     * @param iv            加密算法的初始向量
     * @param request       用户请求request
     * @return 封装结果集
     */
    @RequestMapping("/wechat-group/share")
    @ResponseBody
    public Result shareWechatGroup(@RequestParam String encryptedData,
                                   @RequestParam String iv,
                                   HttpServletRequest request) {
        String sessionKey = redisOpenService.get(RedisKeyEnum.WX_SESSION_KEY.generateKey(SubjectUtil.getToken()));
        User user = userService.getCurrentUser();

        Integer sourceId = user.getSourceId();
        if(!Constant.CJ_SOURCE_ID.equals(sourceId)) {
            return Result.error().msg("非新用户无法领取优惠券！");
        }

        String ipStr = RequestUtil.getIpAdrress(request);
        boolean shareFlag = userWechatGroupShareService.shareWechatGroup(user, sessionKey, encryptedData, iv, ipStr);
        if (shareFlag) {
            UserWechatGroupShareVO groupShareVO = userWechatGroupShareService.getUserShareStatus(user);
            return Result.success().data(groupShareVO).msg("分享成功！");
        } else {
            return Result.error().msg("分享失败，请勿分享到重复微信群！");
        }
    }
}
