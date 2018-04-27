package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.DetailsEnum;
import com.fulu.game.common.enums.OrderDealTypeEnum;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.*;
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



    @Override
    public ICommonDao<Order, Integer> getDao() {
        return orderDao;
    }

    @Override
    public OrderVO submit(int productId,
                          int num,
                          String remark) {
        //todo 确认打手是否已经接单,已经接单需要提示用户等待
        Product product = productService.findById(productId);
        Category category = categoryService.findById(product.getCategoryId());
        //计算订单总价格
        BigDecimal totalMoney = product.getPrice().multiply(new BigDecimal(num));
        //计算单笔订单佣金
        BigDecimal commissionMoney = totalMoney.multiply(category.getCharges());
        //创建订单
        Order order = new Order();
        order.setName(product.getProductName()+"-"+num+"*"+product.getUnit());
        order.setOrderNo(getOrderNo());
        order.setUserId(Constant.DEF_COMMON_USER_ID);
        order.setServiceUserId(product.getUserId());
        order.setRemark(remark);
        order.setIsPlay(false);
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
        orderVO.setProduct(product);
        return orderVO;
    }

    /**
     * 订单支付
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO payOrder(String orderNo){
        Order order =  findByOrderNo(orderNo);
        if(order.getIsPlay()){
           throw new OrderException(orderNo,"重复支付订单!["+order.toString()+"]");
        }
        order.setIsPlay(true);
        order.setStatus(OrderStatusEnum.WAIT_SERVICE.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(),order.getUserId(), DetailsEnum.ORDER_PAY,""+order.getTotalMoney());
        //todo 发送短信通知给陪玩师
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
        if(!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())||!order.getIsPlay()){
            throw new OrderException(order.getOrderNo(),"订单未支付或者状态不是等待陪玩!");
        }
        order.setStatus(OrderStatusEnum.SERVICING.getStatus());
        order.setUpdateTime(new Date());
        update(order);

        //todo 缓存陪玩师接单状态

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
        update(order);
        if(order.getIsPlay()){
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
            ||!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有陪玩中和等待验收的订单才能申诉!");
        }
        order.setStatus(OrderStatusEnum.APPEALING.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //添加申诉文件
        orderDealService.create(orderNo, OrderDealTypeEnum.APPEAL.getType(),remark,fileUrl);
        return orderConvertVo(order);
    }


    /**
     * 打手验收订单
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
        orderDealService.create(orderNo, OrderDealTypeEnum.CHECK.getType(),remark,fileUrl);
        //todo 删除打手接单状态

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
        update(order);

        //todo 给打手加零钱,平台记录收入流水
        return orderConvertVo(order);
    }

    /**
     * 管理员强制完成订单 (大款给打手)
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleCompleteOrder(String orderNo){
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //todo 给打手加零钱,平台记录收入流水
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
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_REFUND.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        if(order.getIsPlay()){
            //todo 全额退款用户
            //记录订单流水
            orderMoneyDetailsService.create(orderNo,order.getUserId(),DetailsEnum.ORDER_USER_CANCEL,"-"+order.getTotalMoney());
        }
        return orderConvertVo(order);
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
