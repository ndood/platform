CREATE TABLE `t_order_admin_remark` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT '订单号',
  `agent_admin_id` int(11) DEFAULT NULL COMMENT '处理的运营ID',
  `agent_admin_name` varchar(255) DEFAULT NULL COMMENT '处理的运营名字',
  `remark` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime DEFAULT NULL COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
  PRIMARY KEY (`id`)
) COMMENT='运营订单备注表';

