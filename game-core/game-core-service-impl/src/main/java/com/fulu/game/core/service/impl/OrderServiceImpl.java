package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.OrderDao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class OrderServiceImpl extends AbsCommonService<Order,Integer> implements OrderService {

    @Autowired
	private OrderDao orderDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;


    @Override
    public ICommonDao<Order, Integer> getDao() {
        return orderDao;
    }

    @Override
    public PageInfo<OrderVO> userList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr) {
        User user =(User)SubjectUtil.getCurrentUser();
        OrderVO params = new OrderVO();
        params.setUserId(user.getId());
        params.setCategoryId(categoryId);
        params.setStatusList(statusArr);
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
        List<OrderVO>  orderVOList = orderDao.findVOByParameter(params);
        for(OrderVO orderVO : orderVOList){
           User server = userService.findById(orderVO.getServiceUserId());
           orderVO.setServerHeadUrl(server.getHeadPortraitsUrl());
           orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        }
        return new PageInfo<>(orderVOList);
    }

    @Override
    public PageInfo<OrderVO> serverList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr) {
        User user =(User)SubjectUtil.getCurrentUser();
        OrderVO params = new OrderVO();
        params.setServiceUserId(user.getId());
        params.setCategoryId(categoryId);
        params.setStatusList(statusArr);
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
        List<OrderVO>  orderVOList = orderDao.findVOByParameter(params);
        for(OrderVO orderVO : orderVOList){
            Category category = categoryService.findById(orderVO.getCategoryId());
            orderVO.setCategoryIcon(category.getIcon());
            orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        }
        return new PageInfo<>(orderVOList);
    }

    @Override
    public OrderVO findOrderDetails(String orderNo){
        Order order = findByOrderNo(orderNo);
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order,orderVO);
        Category category = categoryService.findById(orderVO.getCategoryId());
        orderVO.setCategoryIcon(category.getIcon());
        orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));

        //添加陪玩师信息
        User server= userService.findById(order.getServiceUserId());
        orderVO.setServerHeadUrl(server.getHeadPortraitsUrl());
        orderVO.setServerAge(server.getAge());
        orderVO.setServerGender(server.getGender());
        orderVO.setServerNickName(server.getNickname());
        orderVO.setServerScoreAvg(server.getScoreAvg());

        //添加用户信息
        User user= userService.findById(order.getUserId());
        orderVO.setUserHeadUrl(user.getHeadPortraitsUrl());
        orderVO.setUserNickName(user.getNickname());
        //添加订单商品信息
        OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
        orderVO.setOrderProduct(orderProduct);

        return orderVO;
    }

    @Override
    public int count(Integer serverId,Integer[] statusList, Date startTime, Date endTime) {
        OrderVO params = new OrderVO();
        params.setServiceUserId(serverId);
        params.setStatusList(statusList);
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        int count = orderDao.countByParameter(params);
        return count;
    }

    @Override
    public int weekOrderCount(Integer serverId) {
        Date startTime =  DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return  count(serverId,statusList,startTime,endTime);
    }

    @Override
    public int allOrderCount(Integer serverId) {
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return  count(serverId,statusList,null,null);
    }


    @Override
    public OrderVO submit(int productId,
                          int num,
                          String remark) {
        User user =(User)SubjectUtil.getCurrentUser();
        Product product = productService.findById(productId);
        Category category = categoryService.findById(product.getCategoryId());
        //计算订单总价格
        BigDecimal totalMoney = product.getPrice().multiply(new BigDecimal(num));
        //计算单笔订单佣金
        BigDecimal commissionMoney = totalMoney.multiply(category.getCharges());
        if(commissionMoney.compareTo(totalMoney)>=0){
            throw new OrderException(category.getName(),"订单错误,佣金比订单总价高!");
        }
        //创建订单
        Order order = new Order();
        order.setName(product.getProductName()+" "+num+"*"+product.getUnit());
        order.setOrderNo(getOrderNo());
        order.setUserId(user.getId());
        order.setServiceUserId(product.getUserId());
        order.setCategoryId(product.getCategoryId());
        order.setRemark(remark);
        order.setIsPay(false);
        order.setTotalMoney(totalMoney);
        order.setStatus(OrderStatusEnum.NON_PAYMENT.getStatus());
        order.setCommissionMoney(commissionMoney);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        create(order);
        if(order.getUserId().equals(order.getServiceUserId())){
            throw new ServiceErrorException("陪玩师和下单用户不能一样!");
        }
        //创建订单商品
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderNo(order.getOrderNo());
        orderProduct.setAmount(num);
        orderProduct.setPrice(product.getPrice());
        orderProduct.setProductId(product.getId());
        orderProduct.setProductName(order.getName());
        orderProduct.setCreateTime(new Date());
        orderProduct.setUpdateTime(new Date());
        orderProductService.create(orderProduct);
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order,orderVO);
        orderVO.setOrderProduct(orderProduct);
        return orderVO;
    }

    /**
     * 订单支付
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO payOrder(String orderNo,String orderMoney){
        Order order =  findByOrderNo(orderNo);
        if(order.getIsPay()){
           throw new OrderException(orderNo,"重复支付订单!["+order.toString()+"]");
        }
        order.setIsPay(true);
        order.setStatus(OrderStatusEnum.WAIT_SERVICE.getStatus());
        order.setUpdateTime(new Date());
        order.setPayTime(new Date());
        update(order);
        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(),order.getUserId(), DetailsEnum.ORDER_PAY,orderMoney);
        //发送短信通知给陪玩师
        User server = userService.findById(order.getServiceUserId());
        SMSUtil.sendOrderReceivingRemind(order.getName(),server.getMobile());
        return orderConvertVo(order);
    }


    /**
     * 陪玩师接单
     * @return
     */
    @Override
    public OrderVO serverReceiveOrder(String orderNo){
        Order order =  findByOrderNo(orderNo);
        //只有等待陪玩和已支付的订单才能开始陪玩
        if(!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())||!order.getIsPay()){
            throw new OrderException(order.getOrderNo(),"订单未支付或者状态不是等待陪玩!");
        }
        order.setStatus(OrderStatusEnum.SERVICING.getStatus());
        order.setUpdateTime(new Date());
        update(order);

        OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
        //如果陪玩师开始接单缓存陪玩师开始服务状态 时间:商品数量*小时
        long expire = orderProduct.getAmount()*3600;
        redisOpenService.set(RedisKeyEnum.USER_ORDER_ALREADY_SERVICE_KEY.generateKey(order.getServiceUserId()), order.getOrderNo(),expire);
        return orderConvertVo(order);
    }



    /**
     * 陪玩师取消订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO serverCancelOrder(String orderNo){
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())
            &&!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有陪玩中和等待陪玩的订单才能取消!");
        }
        order.setStatus(OrderStatusEnum.SERVER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //todo 全额退款用户
        //记录订单流水
        orderMoneyDetailsService.create(orderNo,order.getUserId(),DetailsEnum.ORDER_SERVER_CANCEL,"-"+order.getTotalMoney());
        return orderConvertVo(order);
    }

    /**
     * 用户取消订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO userCancelOrder(String orderNo) {
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.NON_PAYMENT.getStatus())
            &&!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有等待陪玩和未支付的订单才能取消!");
        }
        order.setStatus(OrderStatusEnum.USER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        if(order.getIsPay()){
            //todo 全额退款用户
            //记录订单流水
            orderMoneyDetailsService.create(orderNo,order.getUserId(),DetailsEnum.ORDER_USER_CANCEL,"-"+order.getTotalMoney());
        }
        return orderConvertVo(order);
    }


    /**
     * 用户申诉订单
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @Override
    public OrderVO userAppealOrder(String orderNo,String remark,String ... fileUrl){
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())
            &&!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有陪玩中和等待验收的订单才能申诉!");
        }
        order.setStatus(OrderStatusEnum.APPEALING.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //删除打手服务状态
        deleteAlreadyService(order.getServiceUserId());
        //添加申诉文件
        orderDealService.create(orderNo, order.getUserId(),OrderDealTypeEnum.APPEAL.getType(),remark,fileUrl);
        return orderConvertVo(order);
    }


    /**
     * 打手提交验收订单
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @Override
    public OrderVO serverAcceptanceOrder(String orderNo, String remark, String ... fileUrl){
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有陪玩中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.CHECK.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //添加验收文件
        orderDealService.create(orderNo, order.getServiceUserId(),OrderDealTypeEnum.CHECK.getType(),remark,fileUrl);
        deleteAlreadyService(order.getServiceUserId());
        return orderConvertVo(order);
    }

    /**
     * 用户验收订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO userVerifyOrder(String orderNo){
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        shareProfit(order);
        return orderConvertVo(order);
    }


    /**
     * 订单分润
     * @param order
     */
    public void shareProfit(Order order){
        BigDecimal serverMoney = order.getTotalMoney().subtract(order.getCommissionMoney());
        //记录用户流水
        moneyDetailsService.orderSave(serverMoney,order.getServiceUserId(),order.getOrderNo());
        //平台记录收入流水
        platformMoneyDetailsService.createOrderDetails(order.getOrderNo(),order.getCommissionMoney());
    }



    /**
     * 管理员强制完成订单 (大款给打手)
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleCompleteOrder(String orderNo){
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        shareProfit(order);
        return orderConvertVo(order);
    }

    /**
     * 管理员退款用户
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleRefundOrder(String orderNo){
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_REFUND.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        if(order.getIsPay()){
            //todo 全额退款用户
            //记录订单流水
            orderMoneyDetailsService.create(orderNo,order.getUserId(),DetailsEnum.ORDER_USER_CANCEL,"-"+order.getTotalMoney());
        }
        return orderConvertVo(order);
    }


    @Override
    public OrderVO adminHandleNegotiateOrder(String orderNo) {
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_NEGOTIATE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        order.setCommissionMoney(order.getTotalMoney());
        update(order);
        //订单全部金额记录平台流水
        platformMoneyDetailsService.createOrderDetails(order.getOrderNo(),order.getCommissionMoney());
        return orderConvertVo(order);
    }

    /**
     * 陪玩师是否已经在服务用户
     * @return
     */
    @Override
    public Boolean isAlreadyService(Integer serverId){
        return redisOpenService.hasKey(RedisKeyEnum.USER_ORDER_ALREADY_SERVICE_KEY.generateKey(serverId));
    }

    /**
     * 删除陪玩师已经服务用户状态
     * @param serverId
     */
    private void  deleteAlreadyService(Integer serverId){
        redisOpenService.delete(RedisKeyEnum.USER_ORDER_ALREADY_SERVICE_KEY.generateKey(serverId));
    }


    public List<Order> findByStatusList(Integer[] statusList){
        OrderVO orderVO = new OrderVO();
        orderVO.setStatusList(statusList);
        return orderDao.findByParameter(orderVO);
    }



    /**
     * 生成订单号
     * @return
     */
    private String getOrderNo(){
        String orderNo = GenIdUtil.GetOrderNo();
        if(findByOrderNo(orderNo)==null){
            return orderNo;
        }
        else{
            return getOrderNo();
        }
    }


    public Order findByOrderNo(String orderNo){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(orderNo);
        List<Order> orderList =  orderDao.findByParameter(orderVO);
        if(orderList.isEmpty()){
            return null;
        }
        return orderList.get(0);
    }

    private OrderVO orderConvertVo(Order order){
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order,orderVO);
        return orderVO;
    }
}
