

-- 2018/05/14 添加优惠券需求

-- 优惠券组表(未执行)
CREATE TABLE `t_coupon_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deduction` decimal(10,0) DEFAULT NULL COMMENT '减额',
  `is_new_user` tinyint(1) DEFAULT NULL COMMENT '是否是新用户专享',
  `amount` int(11) DEFAULT NULL COMMENT '生成数量',
  `redeem_code` varchar(255) DEFAULT NULL COMMENT '兑换码',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `start_useful_time` datetime DEFAULT NULL COMMENT '有效期开始时间',
  `end_useful_time` datetime DEFAULT NULL COMMENT '有效期结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='优惠券组表';

-- ----------------------------
-- 优惠券表(未执行)
-- ----------------------------
CREATE TABLE `t_coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `coupon_group_id` int(11) DEFAULT NULL,
  `deduction` decimal(2,0) NOT NULL COMMENT '面额',
  `is_new_user` tinyint(1) NOT NULL COMMENT '是否是新用户专享',
  `user_id` int(11) DEFAULT NULL COMMENT '绑定了那个用户',
  `is_use` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否被使用(0:否,1:是)',
  `order_no` varchar(255) DEFAULT NULL COMMENT '订单号',
  `start_useful_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_useful_time` datetime NOT NULL COMMENT '有效期结束时间',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) COMMENT='优惠券表';

-- ----------------------------
-- 分享文案表(未执行)
-- ----------------------------
DROP TABLE IF EXISTS `t_sharing`;
CREATE TABLE `t_sharing`  (
  `id` int(3) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `share_type` int(3) DEFAULT NULL COMMENT '分享类型',
  `gender` tinyint(2) DEFAULT NULL COMMENT '性别',
  `content` varchar(1000) DEFAULT NULL COMMENT '文案内容',
  `status` tinyint(1) DEFAULT NULL COMMENT '是否启用(默认1启用，0不启用)',
  `create_time` datetime(0) DEFAULT NULL COMMENT '记录生成时间',
  `update_time` datetime(0) DEFAULT NULL COMMENT '记录修改时间',
  PRIMARY KEY (`id`)
) COMMENT = '分享文案表';
