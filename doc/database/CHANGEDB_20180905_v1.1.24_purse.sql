CREATE TABLE `t_user_body_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户认证的名字',
  `card_no` varchar(50) DEFAULT NULL COMMENT '用户身份证号',
  `card_url` varchar(255) DEFAULT NULL COMMENT '用户身份证正面照片',
  `card_hand_url` varchar(255) DEFAULT NULL COMMENT '用户手持身份证照片',
  `auth_status` tinyint(4) DEFAULT '0' COMMENT '认证状态  0 未认证  1已通过  2未通过',
  `admin_id` int(11) DEFAULT NULL COMMENT '管理员id',
  `admin_name` varchar(255) DEFAULT NULL COMMENT '管理员名称',
  `remarks` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户身份认证信息表';




CREATE TABLE `t_virtual_pay_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_no` varchar(128) NOT NULL COMMENT '订单号',
  `name` varchar(255) NOT NULL COMMENT '订单名称',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `type` tinyint(1) DEFAULT NULL COMMENT '订单类型(1：虚拟币充值订单，2：余额充值订单)',
  `payment` tinyint(1) DEFAULT NULL COMMENT '支付方式（1：微信支付；2：余额支付）',
  `pay_path` tinyint(1) DEFAULT NULL COMMENT '充值路径（1：公众号；2：安卓；3：IOS）',
  `actual_money` decimal(11,2) DEFAULT NULL COMMENT '实付金额',
  `virtual_money` int(11) DEFAULT NULL COMMENT '虚拟商品价格（对应钻石数量）',
  `money` decimal(11,2) DEFAULT '0.00' COMMENT '充值到平台的金额',
  `order_ip` varchar(128) DEFAULT NULL COMMENT '下单IP',
  `is_pay_callback` tinyint(1) DEFAULT '0' COMMENT '是否接收过微信支付回调(1:已接收,0:未接收)',
  `pay_time` datetime DEFAULT NULL COMMENT '订单支付时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL COMMENT '订单创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`)
) COMMENT='虚拟币和余额充值订单表';

ALTER TABLE `t_cash_draws` ADD COLUMN `type` tinyint(1) DEFAULT '1' COMMENT '提现类型：1：余额提现；2：魅力值提现' after `server_auth`;

ALTER TABLE `t_user` ADD COLUMN `charm_draw_sum` int(11) unsigned DEFAULT '0' COMMENT '累计总提现魅力值' after `charm`;

ALTER TABLE `t_cash_draws` ADD COLUMN `server_auth` tinyint(4) DEFAULT '0' NOT NULL COMMENT '运营是否已处理  0  未处理  1已处理' after `cash_status`;

--t_virtual_product_order加order_no唯一索引
ALTER TABLE `t_virtual_product_order` ADD UNIQUE (`order_no`)

ALTER TABLE `t_virtual_pay_order` ADD COLUMN `pay_path` tinyint(1) DEFAULT NULL COMMENT '充值路径（1：公众号；2：安卓；3：IOS）' after `payment`;
