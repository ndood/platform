CREATE TABLE `t_virtual_pay_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_no` varchar(128) NOT NULL COMMENT '订单号',
  `name` varchar(255) NOT NULL COMMENT '订单名称',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `pay_type` tinyint(1) DEFAULT NULL COMMENT '支付方式（1：虚拟币；2：余额）',
  `actual_money` decimal(11,2) DEFAULT NULL COMMENT '实付金额',
  `virtual_money` int(11) DEFAULT NULL COMMENT '虚拟商品价格（对应钻石数量）',
  `order_ip` varchar(128) DEFAULT NULL COMMENT '下单IP',
  `is_pay_callback` tinyint(1) DEFAULT '0' COMMENT '是否接收过微信支付回调(1:已接收,0:未接收)',
  `pay_time` datetime DEFAULT NULL COMMENT '订单支付时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL COMMENT '订单创建时间',
  PRIMARY KEY (`id`)
) COMMENT='虚拟币充值订单表';