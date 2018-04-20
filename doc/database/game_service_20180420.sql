/*
Navicat MySQL Data Transfer

Source Server         : 本地测试机-周行
Source Server Version : 50721
Source Host           : 10.0.3.105:3306
Source Database       : game_service

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-04-20 19:55:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '父ID',
  `tag_id` int(11) DEFAULT NULL COMMENT '标签组ID',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `name` varchar(255) DEFAULT NULL COMMENT '游戏名称',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态(1激活,0失效)',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `charges` decimal(11,2) DEFAULT NULL COMMENT '手续费',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `t_category_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ----------------------------
-- Records of t_category
-- ----------------------------
INSERT INTO `t_category` VALUES ('1', '0', null, null, '陪玩业务内容', '1', '1', null, '2018-04-19 11:24:22', '2018-04-19 11:24:22');
INSERT INTO `t_category` VALUES ('8', '1', '1', '/img/xx.jpg', '王者荣耀', '0', '100', '0.30', '2018-04-19 15:58:16', '2018-04-19 17:05:23');

-- ----------------------------
-- Table structure for t_channel
-- ----------------------------
DROP TABLE IF EXISTS `t_channel`;
CREATE TABLE `t_channel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `charge` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_channel_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道商表';

-- ----------------------------
-- Records of t_channel
-- ----------------------------

-- ----------------------------
-- Table structure for t_comments
-- ----------------------------
DROP TABLE IF EXISTS `t_comments`;
CREATE TABLE `t_comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL COMMENT '关联商品',
  `star` int(11) DEFAULT NULL COMMENT '几星',
  `content` varchar(1000) DEFAULT NULL COMMENT '名称',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价字典表';

-- ----------------------------
-- Records of t_comments
-- ----------------------------

-- ----------------------------
-- Table structure for t_member
-- ----------------------------
DROP TABLE IF EXISTS `t_member`;
CREATE TABLE `t_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT '状态(0失效,1启用)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='系统管理员表';

-- ----------------------------
-- Records of t_member
-- ----------------------------
INSERT INTO `t_member` VALUES ('1', '管理员', 'admin', '3B74A7C931A8DE5873467004C5B718146039A0FE', '97n8m2iha56fdfg1ojccblh2sdynv619', '1', '2018-04-18 17:35:49', '2018-04-18 17:35:51');

-- ----------------------------
-- Table structure for t_money_details
-- ----------------------------
DROP TABLE IF EXISTS `t_money_details`;
CREATE TABLE `t_money_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述',
  `money` varchar(255) DEFAULT NULL,
  `sum` varchar(255) DEFAULT NULL COMMENT '加零钱后的金额',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_money_details_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='零钱流水表';

-- ----------------------------
-- Records of t_money_details
-- ----------------------------

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) DEFAULT NULL COMMENT '订单号',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `commission_money` decimal(2,0) DEFAULT NULL,
  `total_money` decimal(2,0) DEFAULT NULL,
  `create_time` decimal(2,0) DEFAULT NULL COMMENT '订单价格',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of t_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_order_product
-- ----------------------------
DROP TABLE IF EXISTS `t_order_product`;
CREATE TABLE `t_order_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `product_id` int(11) DEFAULT NULL COMMENT '关联商品',
  `order_no` int(11) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `price` decimal(2,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `t_order_product_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `t_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单技能关联表';

-- ----------------------------
-- Records of t_order_product
-- ----------------------------

-- ----------------------------
-- Table structure for t_person_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_person_tag`;
CREATE TABLE `t_person_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) DEFAULT NULL COMMENT '标签Id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL COMMENT '标签名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_id` (`tag_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_person_tag_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`),
  CONSTRAINT `t_person_tag_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='个人标签关联表';

-- ----------------------------
-- Records of t_person_tag
-- ----------------------------
INSERT INTO `t_person_tag` VALUES ('5', '7', '1', '好看', '2018-04-20 19:46:30', '2018-04-20 19:46:30');
INSERT INTO `t_person_tag` VALUES ('6', '8', '1', '萝莉', '2018-04-20 19:46:30', '2018-04-20 19:46:30');

-- ----------------------------
-- Table structure for t_platform_money_details
-- ----------------------------
DROP TABLE IF EXISTS `t_platform_money_details`;
CREATE TABLE `t_platform_money_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `desc` varchar(255) DEFAULT NULL,
  `money` varchar(255) DEFAULT NULL,
  `sum` decimal(2,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台流水表';

-- ----------------------------
-- Records of t_platform_money_details
-- ----------------------------

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户商品',
  `tech_auth_id` int(11) DEFAULT NULL COMMENT '关联技能',
  `price` decimal(2,0) DEFAULT NULL COMMENT '价格',
  `unit` varchar(255) DEFAULT NULL COMMENT '单位类型',
  `putaway_start_time` datetime DEFAULT NULL COMMENT '上架开始时间',
  `putaway_end_time` datetime DEFAULT NULL COMMENT '上架结束时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `tech_auth_id` (`tech_auth_id`),
  CONSTRAINT `t_product_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `t_product_ibfk_2` FOREIGN KEY (`tech_auth_id`) REFERENCES `t_user_tech_auth` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- Records of t_product
-- ----------------------------

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '父类id（根目录0）',
  `name` varchar(255) DEFAULT NULL COMMENT '标签名称',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别(0:不限制,1:男，2:女)',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1个人标签,2游戏标签)',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `most` int(11) DEFAULT NULL COMMENT '最多选择多少个',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- Records of t_tag
-- ----------------------------
INSERT INTO `t_tag` VALUES ('1', '0', '王者荣耀标签组', '0', '2', null, null, '2018-04-19 16:06:11', '2018-04-19 16:06:11');
INSERT INTO `t_tag` VALUES ('5', '0', '外貌标签组', '0', '1', '10', '2', '2018-04-19 19:04:50', '2018-04-19 19:04:50');
INSERT INTO `t_tag` VALUES ('6', '0', '声音标签组', '0', '1', '10', '2', '2018-04-19 19:05:25', '2018-04-19 19:05:25');
INSERT INTO `t_tag` VALUES ('7', '5', '好看', '1', '1', '2', null, '2018-04-19 19:06:24', '2018-04-19 19:06:24');
INSERT INTO `t_tag` VALUES ('8', '6', '萝莉', '1', '1', '2', null, '2018-04-19 19:06:58', '2018-04-19 19:06:58');
INSERT INTO `t_tag` VALUES ('9', '6', '萝莉音', '1', '1', '2', null, '2018-04-19 19:13:50', '2018-04-19 19:13:50');
INSERT INTO `t_tag` VALUES ('10', '0', '声音标签组', '0', '1', '10', '2', '2018-04-19 19:15:33', '2018-04-19 19:15:33');

-- ----------------------------
-- Table structure for t_tech_attr
-- ----------------------------
DROP TABLE IF EXISTS `t_tech_attr`;
CREATE TABLE `t_tech_attr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL COMMENT '内容ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1销售类型,2段位)',
  `name` varchar(255) DEFAULT NULL COMMENT '游戏ID',
  `status` tinyint(1) DEFAULT '1' COMMENT '技能字典名称(段位)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='技能属性表';

-- ----------------------------
-- Records of t_tech_attr
-- ----------------------------
INSERT INTO `t_tech_attr` VALUES ('1', '8', '1', '王者荣耀销售方式', '1', '2018-04-19 16:16:56', '2018-04-19 16:16:56');
INSERT INTO `t_tech_attr` VALUES ('2', '8', '2', '王者荣耀段位', '1', '2018-04-19 16:38:20', '2018-04-19 16:38:20');

-- ----------------------------
-- Table structure for t_tech_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tech_tag`;
CREATE TABLE `t_tech_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tech_auth_id` int(11) DEFAULT NULL COMMENT '技能ID',
  `tag_id` int(11) DEFAULT NULL COMMENT '标签ID',
  `name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tech_auth_id` (`tech_auth_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `t_tech_tag_ibfk_1` FOREIGN KEY (`tech_auth_id`) REFERENCES `t_user_tech_auth` (`id`),
  CONSTRAINT `t_tech_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能标签关联表';

-- ----------------------------
-- Records of t_tech_tag
-- ----------------------------

-- ----------------------------
-- Table structure for t_tech_value
-- ----------------------------
DROP TABLE IF EXISTS `t_tech_value`;
CREATE TABLE `t_tech_value` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tech_attr_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(255) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tech_attr_id` (`tech_attr_id`),
  CONSTRAINT `t_tech_value_ibfk_1` FOREIGN KEY (`tech_attr_id`) REFERENCES `t_tech_attr` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='技能属性值表';

-- ----------------------------
-- Records of t_tech_value
-- ----------------------------
INSERT INTO `t_tech_value` VALUES ('1', '1', '小时', '1', null, '2018-04-19 16:16:56', '2018-04-19 16:16:56');
INSERT INTO `t_tech_value` VALUES ('3', '2', '白金', '1', '1', '2018-04-19 16:38:20', '2018-04-19 16:38:20');
INSERT INTO `t_tech_value` VALUES ('6', '1', '小时', '1', '0', '2018-04-19 17:16:10', '2018-04-19 17:16:10');
INSERT INTO `t_tech_value` VALUES ('7', '2', '青铜', '0', '1', '2018-04-19 17:19:38', '2018-04-19 17:19:38');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别(1男,2女)',
  `head_portraits_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `salt` varchar(255) DEFAULT NULL COMMENT '密码盐',
  `realname` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `idcard` varchar(255) DEFAULT NULL COMMENT '身份证',
  `type` tinyint(1) DEFAULT NULL COMMENT '1:普通用户,2:打手,3:渠道商',
  `user_info_auth` tinyint(1) DEFAULT '0' COMMENT '信息认证(0未完善,1已完善,2审核通过)',
  `balance` decimal(2,0) DEFAULT NULL COMMENT '零钱',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `mobile` (`mobile`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', '18801285391', 'bin', '1', 'head.jpg', null, null, '王斌', '4211279896674171', '2', '2', null, null, '2018-04-20 12:12:01', '2018-04-20 12:12:03');
INSERT INTO `t_user` VALUES ('2', '18801285392', 'bin', '1', null, '', '', null, null, '0', null, null, null, '2018-04-20 12:12:01', '2018-04-20 12:12:03');
INSERT INTO `t_user` VALUES ('3', '18801285393', 'bin', '1', null, '', '', null, null, '0', null, null, null, '2018-04-20 12:12:01', '2018-04-20 12:12:03');
INSERT INTO `t_user` VALUES ('4', '18801285394', 'bin', '1', null, '', '', null, null, '0', null, null, null, '2018-04-20 12:12:01', '2018-04-20 12:12:03');

-- ----------------------------
-- Table structure for t_user_info_auth
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_auth`;
CREATE TABLE `t_user_info_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `qq` varchar(255) DEFAULT NULL COMMENT 'qq号码',
  `wechat` varchar(255) DEFAULT NULL COMMENT '微信号',
  `main_pic_url` varchar(255) DEFAULT NULL COMMENT '主图',
  `allow_export` tinyint(1) DEFAULT NULL COMMENT '是否允许导出',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `t_user_info_auth_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='信息认证表';

-- ----------------------------
-- Records of t_user_info_auth
-- ----------------------------
INSERT INTO `t_user_info_auth` VALUES ('9', '1', '18801285391', '327075297', 'bu', 'main.jpg', '1', '2018-04-20 19:46:30', '2018-04-20 19:46:30');

-- ----------------------------
-- Table structure for t_user_info_auth_file
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_auth_file`;
CREATE TABLE `t_user_info_auth_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `info_auth_id` int(11) DEFAULT NULL COMMENT '关联信息认证ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1图片,2声音)',
  `url` varchar(255) DEFAULT NULL,
  `ext` varchar(255) DEFAULT NULL COMMENT '扩展名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `info_auth_id` (`info_auth_id`),
  CONSTRAINT `t_user_info_auth_file_ibfk_1` FOREIGN KEY (`info_auth_id`) REFERENCES `t_user_info_auth` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='信息认证文件表（图片、声音）';

-- ----------------------------
-- Records of t_user_info_auth_file
-- ----------------------------
INSERT INTO `t_user_info_auth_file` VALUES ('7', '写真1', '9', '1', '1.jpg', null, '2018-04-20 19:46:30', null);
INSERT INTO `t_user_info_auth_file` VALUES ('8', '写真2', '9', '1', '2.jpg', null, '2018-04-20 19:46:30', null);
INSERT INTO `t_user_info_auth_file` VALUES ('9', '语音介绍', '9', '2', 'voice.mp', null, '2018-04-20 19:46:30', null);

-- ----------------------------
-- Table structure for t_user_info_file
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_file`;
CREATE TABLE `t_user_info_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `user_id` int(11) DEFAULT NULL,
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1图片,2声音)',
  `url` varchar(255) DEFAULT NULL COMMENT '文件URL',
  `ext` varchar(255) DEFAULT NULL COMMENT '文件扩展名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_user_info_file_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息文件表(图片、声音)';

-- ----------------------------
-- Records of t_user_info_file
-- ----------------------------
INSERT INTO `t_user_info_file` VALUES ('7', '身份证国徽面', '1', '1', 'idCardEmble.jpg', null, '2018-04-20 19:46:30', null);
INSERT INTO `t_user_info_file` VALUES ('8', '身份证人像面', '1', '1', 'idCardHead.jpg', null, '2018-04-20 19:46:30', null);
INSERT INTO `t_user_info_file` VALUES ('9', '手持身份证照片', '1', '1', 'idCardHand.jpg', null, '2018-04-20 19:46:30', null);

-- ----------------------------
-- Table structure for t_user_tech_auth
-- ----------------------------
DROP TABLE IF EXISTS `t_user_tech_auth`;
CREATE TABLE `t_user_tech_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL COMMENT '游戏ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL COMMENT '状态(0未审核,1审核通过)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_user_tech_auth_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`),
  CONSTRAINT `t_user_tech_auth_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能认证表';

-- ----------------------------
-- Records of t_user_tech_auth
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_tech_info
-- ----------------------------
DROP TABLE IF EXISTS `t_user_tech_info`;
CREATE TABLE `t_user_tech_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tech_auth_id` int(11) DEFAULT NULL COMMENT '技能认证ID',
  `tech_attr_id` int(11) DEFAULT NULL COMMENT '技能属性ID',
  `attr` varchar(255) DEFAULT NULL COMMENT '技能属性名称',
  `tech_value_id` int(11) DEFAULT NULL COMMENT '技能属性值ID',
  `value` varchar(255) DEFAULT NULL COMMENT '技能属性值',
  `status` tinyint(4) DEFAULT NULL,
  `rank` varchar(255) DEFAULT NULL COMMENT '排名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tech_auth_id` (`tech_auth_id`),
  KEY `tech_attr_id` (`tech_attr_id`),
  KEY `t_user_tech_info_ibfk_3` (`tech_value_id`),
  CONSTRAINT `t_user_tech_info_ibfk_1` FOREIGN KEY (`tech_auth_id`) REFERENCES `t_user_tech_auth` (`id`),
  CONSTRAINT `t_user_tech_info_ibfk_2` FOREIGN KEY (`tech_attr_id`) REFERENCES `t_tech_attr` (`id`),
  CONSTRAINT `t_user_tech_info_ibfk_3` FOREIGN KEY (`tech_value_id`) REFERENCES `t_tech_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户技能表';

-- ----------------------------
-- Records of t_user_tech_info
-- ----------------------------
