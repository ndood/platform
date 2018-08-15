package com.fulu.game.h5.controller.fenqile;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.SystemException;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.order.H5OrderServiceImpl;
import com.fulu.game.h5.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:54
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {
    private final UserService userService;
    private final RedisOpenServiceImpl redisOpenService;
    private final H5OrderServiceImpl orderService;

    @Autowired
    public OrderController(UserService userService,
                           RedisOpenServiceImpl redisOpenService,
                           H5OrderServiceImpl orderService) {
        this.userService = userService;
        this.redisOpenService = redisOpenService;
        this.orderService = orderService;
    }

    /**
     * 提交订单
     *
     * @param productId   商品id
     * @param request     请求
     * @param num         订单数量
     * @param couponNo    优惠券编码
     * @param sessionkey  sessionkey（相当于下单的令牌）
     * @param contactType 联系方式类型(1：手机号；2：QQ号；3：微信号)
     * @param contactInfo 联系方式
     * @return 封装结果集
     */
    @RequestMapping(value = "submit")
    public Result submit(@RequestParam Integer productId,
                         HttpServletRequest request,
                         @RequestParam Integer num,
                         String couponNo,
                         @RequestParam String sessionkey,
                         Integer contactType,
                         String contactInfo) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:productId:{};num:{};couponNo:{};sessionkey:{};userId:{}",
                    productId, num, couponNo, sessionkey, user.getId());
            throw new SystemException(SystemException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        try {
            String ip = RequestUtil.getIpAdrress(request);
            String orderNo = orderService.submit(productId, num, couponNo, ip, contactType, contactInfo);
            return Result.success().data(orderNo).msg("创建订单成功!");
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }

    /**
     * 订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @RequestMapping(value = "/list")
    public Result orderList(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize) {
        PageInfo<OrderDetailsVO> page = orderService.list(pageNum, pageSize);
        return Result.success().data(page);
    }
}
