

ALTER TABLE `t_order` ADD COLUMN `type`  tinyint(1) NULL COMMENT '订单类型(1：普通订单，2：集市订单)' AFTER `category_id`;
ALTER TABLE `t_order` ADD COLUMN `channel_id`  int(11) NULL COMMENT '渠道商ID' AFTER `user_id`;
ALTER TABLE `t_order` ADD COLUMN `order_ip`  varchar(128) NULL COMMENT '订单IP' AFTER `actual_money`;
ALTER TABLE `t_order` ADD COLUMN `receiving_time`  varchar(128) NULL COMMENT '接单时间' AFTER `order_ip`;

ALTER TABLE `t_order`  MODIFY COLUMN `user_id` int(11) NULL COMMENT '下单用户ID';
ALTER TABLE `t_order` MODIFY COLUMN `service_user_id` int(11) NULL COMMENT '陪玩师用户ID';

--同步之前的所有订单类型
update `t_order` set type = 1;


-- 添加推送间隔字段
ALTER TABLE `t_user_info_auth` ADD COLUMN `push_time_interval`  float(11,2) NULL DEFAULT 30 COMMENT '推送时间间隔(0表示永不推送)' AFTER `main_pic_url`;

-- 修改管理员可以申诉订单，集市订单申诉后userId为空
ALTER TABLE `t_order_deal` MODIFY COLUMN `user_id`  int(11) NULL AFTER `order_no`


DROP TABLE IF EXISTS `t_cdk`;
CREATE TABLE `t_cdk`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `series` varchar(128) NOT NULL COMMENT '序列号',
  `price` decimal(11, 2) NOT NULL COMMENT '单价',
  `type` varchar(255) NOT NULL COMMENT '类型',
  `group_id` int(11) NOT NULL COMMENT '批次id',
  `channel_id` int(11) NOT NULL COMMENT '渠道商id',
  `category_id` int(11) NOT NULL COMMENT '游戏id',
  `is_use` tinyint(1) NOT NULL COMMENT '使用状态(0未使用，1已使用)',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否可用(0不可用，1可用)',
  `order_no` varchar(255) DEFAULT NULL COMMENT '使用订单号',
  `update_time` datetime(0) DEFAULT NULL COMMENT '使用时间',
  `create_time` datetime(0) NOT NULL COMMENT '生成时间',
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `series`(`series`)
) COMMENT = 'cdk记录表' ;

-- ----------------------------
-- Table structure for t_cdk_group
-- ----------------------------
DROP TABLE IF EXISTS `t_cdk_group`;
CREATE TABLE `t_cdk_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `type` varchar(128)  NOT NULL COMMENT '类型(卢克，安图恩，H阿古斯)',
  `status` tinyint(1) NOT NULL COMMENT '启用状态(1为启用，0为关闭)',
  `category_id` int(11) NOT NULL COMMENT '游戏id',
  `price` decimal(11, 2) NOT NULL COMMENT '单价',
  `amount` int(11) NOT NULL COMMENT '数量',
  `admin_id` int(11) NOT NULL COMMENT '操作人id',
  `admin_name` varchar(255)  NOT NULL COMMENT '操作人用户名',
  `channel_id` int(11) DEFAULT NULL COMMENT '渠道商id',
  `channel_name` varchar(255) DEFAULT NULL COMMENT '渠道商名',
  `create_time` datetime(0) NOT NULL COMMENT '生成时间',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) COMMENT = 'cdk批次表(目前仅盛天使用)';

-- ----------------------------
-- Table structure for t_channel
-- ----------------------------
DROP TABLE IF EXISTS `t_channel`;
CREATE TABLE `t_channel`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) NOT NULL COMMENT '渠道商名',
  `appid` varchar(255) NOT NULL COMMENT '渠道商唯一标识',
  `appkey` varchar(255) NOT NULL COMMENT '渠道商访问的token',
  `balance` decimal(11, 2) NOT NULL COMMENT '余额(初始为0.00)',
  `admin_id` int(11) NOT NULL COMMENT '添加人id',
  `admin_name` varchar(255) NOT NULL COMMENT '添加人用户名',
  `create_time` datetime(0) NOT NULL COMMENT '生成时间',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `appid`(`appid`)
) COMMENT = '渠道商表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_channel_cash_details
-- ----------------------------
DROP TABLE IF EXISTS `t_channel_cash_details`;
CREATE TABLE `t_channel_cash_details`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `admin_id` int(11) DEFAULT NULL COMMENT '管理员id',
  `admin_name` varchar(255) DEFAULT NULL COMMENT '管理员用户名',
  `channel_id` int(11) NOT NULL COMMENT '渠道id',
  `action` tinyint(1) NOT NULL COMMENT '加款:1,扣款:2,退款:3',
  `money` decimal(11, 2) NOT NULL COMMENT '本次金额',
  `sum` decimal(11, 2) NOT NULL COMMENT '当前余额',
  `order_no` varchar(128) DEFAULT NULL COMMENT '订单号',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL COMMENT '生成时间',
  PRIMARY KEY (`id`)
) COMMENT = '渠道商金额流水表';

-- ----------------------------
-- Table structure for t_regist_source
-- ----------------------------
DROP TABLE IF EXISTS `t_regist_source`;
CREATE TABLE `t_regist_source`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) NOT NULL COMMENT '注册来源名',
  `admin_id` int(11) NOT NULL COMMENT '操作人id',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `wxcode_url` varchar(255) DEFAULT NULL COMMENT '小程序码url',
  `create_time` datetime(0) NOT NULL COMMENT '生成时间',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USIN   BTREE
) COMMENT = '注册来源表';


CREATE TABLE `t_order_market_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `category_id` int(11) DEFAULT NULL COMMENT '游戏分类ID',
  `product_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `type` varchar(255) DEFAULT NULL COMMENT '服务类型',
  `mobile` varchar(255) NOT NULL COMMENT '手机号',
  `game_area` varchar(255) DEFAULT NULL COMMENT '游戏区服',
  `rolename` varchar(255) DEFAULT NULL COMMENT '角色名',
  `price` decimal(10,0) NOT NULL COMMENT '价格',
  `amount` int(11) NOT NULL COMMENT '数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='集市订单商品表';