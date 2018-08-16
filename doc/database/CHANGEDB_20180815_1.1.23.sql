--分期乐订单拓展表
DROP TABLE IF EXISTS `t_fenqile_order`;
CREATE TABLE `t_fenqile_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(128) NOT NULL COMMENT '订单编号',
  `payment_no` varchar(512) DEFAULT NULL COMMENT '支付编号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='分期乐订单拓展表';

--分期乐对账表
DROP TABLE IF EXISTS `t_fenqile_reconciliation`;
CREATE TABLE `t_fenqile_reconciliation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_no` varchar(128) NOT NULL COMMENT '订单编号',
  `status` tinyint(1) DEFAULT '0' COMMENT '对账状态（0：未对账（默认）；1：已对账）',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '对账金额',
  `commission_money` decimal(11,2) DEFAULT NULL COMMENT '分期乐平台佣金',
  `process_time` datetime DEFAULT NULL COMMENT '对账时间',
  `admin_id` int(11) DEFAULT NULL COMMENT '对账人id',
  `admin_name` varchar(255) DEFAULT NULL COMMENT '对账人用户名',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='分期乐对账表';

--分期乐对账记录表
DROP TABLE IF EXISTS `t_fenqile_recon_record`;
CREATE TABLE `t_fenqile_recon_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `start_time` datetime DEFAULT NULL COMMENT '订单开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '订单结束时间',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '对账金额',
  `order_count` int(11) DEFAULT NULL COMMENT '订单数量',
  `process_time` datetime DEFAULT NULL COMMENT '对账时间',
  `admin_id` int(11) DEFAULT NULL COMMENT '对账人id',
  `admin_name` varchar(255) DEFAULT NULL COMMENT '对账人用户名',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='分期乐对账记录表';

CREATE TABLE `t_thirdparty_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `fql_openid` varchar(255) DEFAULT NULL COMMENT '分期乐openId',
  `fql_mobile` varchar(255) DEFAULT NULL COMMENT '分期用户手机号',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `fql_openid` (`fql_openid`)
) COMMENT='第三方用户信息';