
-- 2018/05/14 添加系统配置需求
-- 系统配置表(已执行)
CREATE TABLE `t_sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) DEFAULT NULL COMMENT '配置值类型(1:boolean,2:string,3:json)',
  `name` varchar(255) DEFAULT NULL COMMENT '配置名',
  `value` varchar(255) DEFAULT NULL COMMENT '配置值',
  `note` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='系统配置表';

-- ----------------------------
-- Records of t_sys_config
-- ----------------------------
INSERT INTO `t_sys_config` VALUES ('1', '1', 'MMCON', 'CLOSE', 'IM开启关闭(CLOSE:全关,ALL:全打开,ANDROID:安卓打开,IOS:IOS打开)', '2018-05-14 15:07:46', '2018-05-14 15:07:48');
INSERT INTO `t_sys_config` VALUES ('2', '1', 'PAYCON', 'CLOSE', '支付开启关闭(CLOSE:全关,ALL:全打开,ANDROID:安卓打开,IOS:IOS打开)', '2018-05-14 15:08:16', '2018-05-14 15:08:19');
ALTER TABLE `t_sys_config` DROP COLUMN `type`;

-- 2018/05/14 添加优惠券和分享需求(未执行)
DROP TABLE IF EXISTS `t_sharing`;
CREATE TABLE `t_sharing` (
  `id` int(3) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `share_type` int(3) DEFAULT NULL COMMENT '分享类型',
  `gender` tinyint(2) DEFAULT NULL COMMENT '性别',
  `content` varchar(1000) DEFAULT NULL COMMENT '文案内容',
  `status` tinyint(1) DEFAULT NULL COMMENT '是否启用(默认1启用，0不启用)',
  `create_time` datetime DEFAULT NULL COMMENT '记录生成时间',
  `update_time` datetime DEFAULT NULL COMMENT '记录修改时间',
  PRIMARY KEY (`id`)
)  COMMENT='分享文案表';

-- ----------------------------
-- Table structure for t_coupon_group
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon_group`;
CREATE TABLE `t_coupon_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deduction` decimal(11,2) DEFAULT NULL COMMENT '减额',
  `is_new_user` tinyint(1) DEFAULT NULL COMMENT '是否是新用户专享',
  `amount` int(11) DEFAULT NULL COMMENT '生成数量',
  `redeem_code` varchar(128) DEFAULT NULL COMMENT '兑换码',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `start_useful_time` datetime DEFAULT NULL COMMENT '有效期开始时间',
  `end_useful_time` datetime DEFAULT NULL COMMENT '有效期结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `redeem_code` (`redeem_code`)
)  COMMENT='优惠券组表';

-- ----------------------------
-- Table structure for t_coupon
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon`;
CREATE TABLE `t_coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `coupon_no` varchar(128) DEFAULT NULL COMMENT '优惠券编码(优惠券唯一标识)',
  `coupon_group_id` int(11) NOT NULL COMMENT '优惠券组ID',
  `deduction` decimal(11,2) NOT NULL COMMENT '面额',
  `is_new_user` tinyint(1) NOT NULL COMMENT '是否是新用户专享',
  `user_id` int(11) DEFAULT NULL COMMENT '绑定了那个用户',
  `mobile` varchar(128) DEFAULT NULL COMMENT '领取手机号',
  `is_use` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否被使用(0:否,1:是)',
  `order_no` varchar(128) DEFAULT NULL COMMENT '订单号',
  `start_useful_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_useful_time` datetime NOT NULL COMMENT '有效期结束时间',
  `receive_time` datetime DEFAULT NULL COMMENT '领取时间',
  `is_first_receive` tinyint(1) DEFAULT '1' COMMENT '是否是首次领取',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `receive_ip` varchar(128) DEFAULT NULL COMMENT '领取IP',
  `use_ip` varchar(128) DEFAULT NULL COMMENT '使用IP',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `coupon_no` (`coupon_no`),
  KEY `coupon_group_id` (`coupon_group_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_coupon_ibfk_1` FOREIGN KEY (`coupon_group_id`) REFERENCES `t_coupon_group` (`id`),
  CONSTRAINT `t_coupon_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
)  COMMENT='优惠券表';


-- ----------------------------
-- Table structure for t_coupon_grant
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon_grant`;
CREATE TABLE `t_coupon_grant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `coupon_group_id` int(11) NOT NULL COMMENT '优惠卷组ID',
  `remark` varchar(255) NOT NULL COMMENT '发放原因',
  `redeem_code` varchar(255) DEFAULT NULL COMMENT '优惠券兑换码',
  `deduction` decimal(10,0) NOT NULL COMMENT '优惠券面额',
  `is_new_user` tinyint(1) NOT NULL COMMENT '是否是新用户专享',
  `start_useful_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_useful_time` datetime NOT NULL COMMENT '有效期结束时间',
  `admin_id` int(11) DEFAULT NULL COMMENT '发放人ID',
  `admin_name` varchar(255) DEFAULT NULL COMMENT '发放人名称',
  `create_time` datetime NOT NULL COMMENT '发放时间',
  PRIMARY KEY (`id`),
  KEY `coupon_group_id` (`coupon_group_id`),
  CONSTRAINT `t_coupon_grant_ibfk_1` FOREIGN KEY (`coupon_group_id`) REFERENCES `t_coupon_group` (`id`)
)  COMMENT='优惠券发放记录';

-- ----------------------------
-- Table structure for t_coupon_grant_user
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon_grant_user`;
CREATE TABLE `t_coupon_grant_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `coupon_no` varchar(128) DEFAULT NULL,
  `coupon_grant_id` int(11) NOT NULL COMMENT '发放记录ID',
  `user_id` int(11) DEFAULT NULL COMMENT '发放用户ID',
  `mobile` varchar(255) NOT NULL COMMENT '发放手机号',
  `is_success` tinyint(4) DEFAULT NULL COMMENT '是否发放成功',
  `error_cause` varchar(255) DEFAULT NULL COMMENT '发放错误原因',
  `create_time` datetime NOT NULL COMMENT '发放时间',
  PRIMARY KEY (`id`),
  KEY `coupon_grant_id` (`coupon_grant_id`),
  CONSTRAINT `t_coupon_grant_user_ibfk_1` FOREIGN KEY (`coupon_grant_id`) REFERENCES `t_coupon_grant` (`id`)
)  COMMENT='优惠券发放用户';




---更新订单字段需求
ALTER TABLE `t_order`
ADD COLUMN `actual_money`  decimal(11,2) NULL DEFAULT NULL  COMMENT '实付金额' AFTER `total_money` ;

ALTER TABLE `t_order`
ADD COLUMN `coupon_money`  decimal(11,2) NULL DEFAULT NULL COMMENT '优惠券金额' AFTER `total_money` ;

ALTER TABLE `t_order`
ADD COLUMN `coupon_no` varchar(128) NULL DEFAULT NULL COMMENT '优惠券编号' AFTER `order_no`;
---批量更新之前actual_money为空的字段
UPDATE t_order SET actual_money = total_money WHERE actual_money is null;

ALTER TABLE `t_sys_config`
ADD COLUMN `version` varchar(50) NULL DEFAULT NULL COMMENT '版本号';
UPDATE t_sys_config SET version = '1.0.2';

---更新订单字段需求
ALTER TABLE `t_coupon_group`
ADD COLUMN `admin_id`  int(11) NULL DEFAULT NULL  COMMENT '管理员ID' AFTER `end_useful_time` ;

ALTER TABLE `t_coupon_group`
ADD COLUMN `admin_name`  varchar(255) NULL DEFAULT NULL  COMMENT '管理员名称' AFTER `admin_id` ;