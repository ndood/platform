-- 需要确认接口删除
-- /order/user/details
-- /server/details


CREATE TABLE `t_grading_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL COMMENT '游戏分类',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1:开黑,2：包赢,3:精准上分)',
  `pid` int(11) NOT NULL COMMENT '父类属性id',
  `name` varchar(255) NOT NULL COMMENT '单位名称',
  `rank` int(11) DEFAULT NULL COMMENT '权重',
  `price` decimal(11,2) DEFAULT NULL COMMENT '价格',
  `admin_id` int(11) DEFAULT NULL COMMENT '最后修改管理员',
  `admin_name` varchar(255) DEFAULT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='段位定级价格表';

-- ----------------------------
-- Records of t_grading_price
-- ----------------------------
INSERT INTO `t_grading_price` VALUES ('1', null, null, '0', '上分', null, null, null, null, '2018-07-23 19:24:24', null);
INSERT INTO `t_grading_price` VALUES ('2', '30', '1', '1', '开黑', null, null, null, null, '2018-07-23 19:26:51', null);
INSERT INTO `t_grading_price` VALUES ('3', '30', '2', '1', '包赢', null, null, null, null, '2018-07-23 19:31:17', null);
INSERT INTO `t_grading_price` VALUES ('4', '30', '3', '1', '精准上分', null, null, null, null, '2018-07-23 19:31:40', null);
INSERT INTO `t_grading_price` VALUES ('103', '30', '3', '4', '青铜三', '1', '0.00', null, null, '2018-07-24 12:02:37', null);
INSERT INTO `t_grading_price` VALUES ('104', '30', '3', '103', '1星', '2', '5.00', null, null, '2018-07-24 12:02:37', null);
INSERT INTO `t_grading_price` VALUES ('105', '30', '3', '103', '2星', '3', '5.00', null, null, '2018-07-24 12:04:49', null);
INSERT INTO `t_grading_price` VALUES ('106', '30', '3', '4', '白银一', '4', '0.00', null, null, '2018-07-24 12:02:37', null);
INSERT INTO `t_grading_price` VALUES ('107', '30', '3', '106', '1星', '5', '6.00', null, null, '2018-07-24 12:02:37', null);
INSERT INTO `t_grading_price` VALUES ('108', '30', '3', '106', '2星', '6', '6.00', null, null, '2018-07-24 12:04:49', null);

-- ----------------------------
-- Table structure for t_order_point_product
-- ----------------------------
CREATE TABLE `t_order_point_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(128) NOT NULL COMMENT '订单号',
  `point_type` tinyint(4) NOT NULL COMMENT '上分分类',
  `category_id` int(11) NOT NULL COMMENT '游戏分类ID',
  `area_id` int(11) DEFAULT NULL COMMENT '大区ID',
  `grading_price_id` int(11) DEFAULT NULL COMMENT '段位等级',
  `target_grading_price_id` int(11) DEFAULT NULL COMMENT '目标段位等级',
  `category_name` varchar(255) DEFAULT NULL COMMENT '游戏名称',
  `category_icon` varchar(255) DEFAULT NULL COMMENT '游戏图标',
  `account_info` varchar(255) DEFAULT NULL COMMENT '账号信息',
  `order_choice` varchar(255) DEFAULT NULL COMMENT '订单选择',
  `price` decimal(10,0) NOT NULL,
  `amount` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`) USING BTREE
) COMMENT='上分订单详情';


CREATE TABLE `t_user_contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `type` int(11) DEFAULT NULL COMMENT '联系方式类型(1：微信号；2：QQ号；3：手机号)',
  `contact` varchar(255) DEFAULT NULL COMMENT '联系方式',
  `is_default` tinyint(4) DEFAULT NULL COMMENT '是否默认',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='用户联系方式表';


CREATE TABLE `t_user_auto_receive_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `tech_auth_id` int(11) NOT NULL COMMENT '游戏技能ID',
  `category_id` int(11) NOT NULL COMMENT '游戏ID',
  `area_id` int(11) DEFAULT NULL COMMENT '选择大区ID（为空则为全部）',
  `start_rank` int(11) DEFAULT NULL COMMENT '接单范围开始',
  `end_rank` int(11) DEFAULT NULL COMMENT '接单范围结束',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `rank_ids` varchar(255) DEFAULT NULL COMMENT '段位ids',
  `order_num` int(11) DEFAULT NULL COMMENT '自动派单数',
  `order_complete_num` int(11) DEFAULT NULL COMMENT '自动派单完成数',
  `order_cancel_num` int(11) DEFAULT NULL COMMENT '派单取消数',
  `order_dispute_num` int(11) DEFAULT NULL COMMENT '派单协商仲裁数',
  `order_fail_percent` double(10,2) DEFAULT NULL COMMENT '派单失败率',
  `user_auto_setting` tinyint(1) NOT NULL COMMENT '用户自己自动接单设置',
  `admin_id` int(11) DEFAULT NULL,
  `admin_name` varchar(255) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标记 1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tech_auth_id` (`tech_auth_id`)
)  COMMENT='自动接单设置表';

--添加系统设置表
CREATE TABLE `t_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL COMMENT '设置类型',
  `val` varchar(255) NOT NULL COMMENT '系统值',
  `admin_id` int(11) NOT NULL,
  `admin_name` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='系统设置表';

INSERT INTO `t_setting` VALUES ('1', '1', '3', '1', '1', '2018-07-30 11:01:20');

--修改用户表
ALTER TABLE `t_user` ADD COLUMN `point_open_id` varchar(128) DEFAULT NULL COMMENT '开黑上分openid' after `open_id`;
ALTER TABLE `t_user` ADD COLUMN `public_open_id` varchar(128) DEFAULT NULL COMMENT '微信公众号openid' after `point_open_id`;
ALTER TABLE `t_user` ADD COLUMN `union_id` varchar(128) DEFAULT NULL COMMENT '微信生态唯一标识' after `public_open_id`;

ALTER TABLE `t_user` ADD UNIQUE INDEX (`point_open_id`) USING BTREE;
ALTER TABLE `t_user` ADD UNIQUE INDEX (`public_open_id`) USING BTREE;
ALTER TABLE `t_user` ADD UNIQUE INDEX (`union_id`) USING BTREE;

--修改游戏分类表
ALTER TABLE `t_category` ADD COLUMN `is_point` tinyint(1)  DEFAULT NULL COMMENT '是否是上分平台' after `status`;
UPDATE `t_category` SET `is_point` = true WHERE id = 30;
--修改推送消息表
ALTER TABLE `t_push_msg` ADD COLUMN `platform` tinyint(1)  DEFAULT NULL COMMENT '微信平台号' after `content`;

--修改微信formid表
ALTER TABLE `t_wechat_formid` ADD COLUMN `platform` tinyint(1)  DEFAULT NULL COMMENT '微信平台号' after `form_id`;
ALTER TABLE `t_wechat_formid` ADD COLUMN `open_id` varchar(128)  DEFAULT NULL COMMENT 'openId' after `platform`;
UPDATE `t_wechat_formid` as f SET `open_id` = (SELECT `open_id` FROM `t_user` as u WHERE u.`id` =f.`user_id` ),`platform` =1;

--修改平台配置表
ALTER TABLE `t_sys_config` ADD COLUMN `type` tinyint(1)  DEFAULT NULL COMMENT '平台类型' after `id`;

--修改单位表
ALTER TABLE `t_sales_mode` 	ADD COLUMN `type` tinyint(1) NULL COMMENT '平台统一单位类型(1:小时,2:局,3:次,4:首)' AFTER `category_id`;
ALTER TABLE `t_sales_mode` 	ADD COLUMN `del_flag` tinyint(1) NULL COMMENT '删除标记(1:删除，0:正常)' AFTER `update_time`;
UPDATE `t_sales_mode` SET `del_flag` = false;

--修改商品表
ALTER TABLE `t_product` 	ADD COLUMN `is_activate` tinyint(1) NULL COMMENT '是否激活' AFTER `status`;
update t_product set is_activate = TRUE;

--修改用户技能表
ALTER TABLE `t_user_tech_auth` ADD COLUMN `is_activate` tinyint(1)  DEFAULT NULL COMMENT '是否激活' after `status`;
update t_user_tech_auth set is_activate = FALSE;

--修改发放优惠券接口
ALTER TABLE `t_coupon_grant_user` 	MODIFY COLUMN `mobile` varchar(255)  NULL COMMENT '发放手机号';



--- 未更新脚本

UPDATE `t_wechat_formid` as f SET `open_id` = (SELECT `open_id` FROM `t_user` as u WHERE u.`id` =f.`user_id` ),`platform` =1;
-- 删除商品表多余的单位内容(修改技能单位后操作)

UPDATE `t_product` p SET `del_flag` = 1  WHERE p.`sales_mode_id` in (SELECT id FROM `t_sales_mode` WHERE `del_flag` =1)

--- 未更新脚本