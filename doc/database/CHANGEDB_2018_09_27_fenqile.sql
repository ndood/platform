CREATE TABLE `t_fenqile_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(128) NOT NULL COMMENT '订单编号',
  `fenqile_no` varchar(512) DEFAULT NULL COMMENT '支付编号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`) USING BTREE
)  COMMENT='分期乐订单拓展表';

