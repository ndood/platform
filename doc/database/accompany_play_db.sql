/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : accompany_play_db

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2018-04-17 20:07:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '父ID',
  `name` varchar(255) DEFAULT NULL COMMENT '游戏名称',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态(1激活,0失效)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ----------------------------
-- Records of t_category
-- ----------------------------

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
  PRIMARY KEY (`id`)
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
  `star` int(11) DEFAULT NULL COMMENT '几星',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
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
  `status` tinyint(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统管理员表';

-- ----------------------------
-- Records of t_member
-- ----------------------------

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
  PRIMARY KEY (`id`)
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of t_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_order_tech
-- ----------------------------
DROP TABLE IF EXISTS `t_order_tech`;
CREATE TABLE `t_order_tech` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` int(11) DEFAULT NULL,
  `user_tech_auth_id` int(11) DEFAULT NULL,
  `tech_price` decimal(2,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单技能关联表';

-- ----------------------------
-- Records of t_order_tech
-- ----------------------------

-- ----------------------------
-- Table structure for t_person_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_person_tag`;
CREATE TABLE `t_person_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人标签关联表';

-- ----------------------------
-- Records of t_person_tag
-- ----------------------------

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
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '父类id（根目录0）',
  `name` varchar(255) DEFAULT NULL COMMENT '标签名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- Records of t_tag
-- ----------------------------

-- ----------------------------
-- Table structure for t_tech_attr
-- ----------------------------
DROP TABLE IF EXISTS `t_tech_attr`;
CREATE TABLE `t_tech_attr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL,
  `name` int(11) DEFAULT NULL COMMENT '游戏ID',
  `status` varchar(255) DEFAULT NULL COMMENT '技能字典名称(段位)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能属性表';

-- ----------------------------
-- Records of t_tech_attr
-- ----------------------------

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
  PRIMARY KEY (`id`)
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能属性值表';

-- ----------------------------
-- Records of t_tech_value
-- ----------------------------

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别',
  `head_portraits_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `salt` varchar(255) DEFAULT NULL COMMENT '密码盐',
  `realname` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `idcard` varchar(255) DEFAULT NULL COMMENT '身份证',
  `type` tinyint(1) DEFAULT NULL COMMENT '1:普通用户,2:打手,3:渠道商',
  `user_info_auth` tinyint(1) DEFAULT '0' COMMENT '信息认证(0未完善,1已完善,2审核通过)',
  `balance` decimal(2,0) DEFAULT NULL,
  `state` tinyint(1) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of t_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_info_auth
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_auth`;
CREATE TABLE `t_user_info_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `qq` varchar(255) DEFAULT NULL COMMENT 'qq号码',
  `wechat` varchar(255) DEFAULT NULL COMMENT '微信号',
  `main_pic_url` varchar(255) DEFAULT NULL COMMENT '主图',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信息认证表';

-- ----------------------------
-- Records of t_user_info_auth
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_info_auth_file
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_auth_file`;
CREATE TABLE `t_user_info_auth_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `info_auth_id` int(11) DEFAULT NULL,
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1图片,2声音)',
  `url` varchar(255) DEFAULT NULL,
  `ext` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信息认证文件表（图片、声音）';

-- ----------------------------
-- Records of t_user_info_auth_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_info_file
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_file`;
CREATE TABLE `t_user_info_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1图片,2声音)',
  `url` varchar(255) DEFAULT NULL,
  `ext` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息文件表(图片、声音)';

-- ----------------------------
-- Records of t_user_info_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_tech_auth
-- ----------------------------
DROP TABLE IF EXISTS `t_user_tech_auth`;
CREATE TABLE `t_user_tech_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL COMMENT '游戏ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL COMMENT '激活(0未上架,1上架)',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态(0未审核,1审核通过)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
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
  `attr` int(11) DEFAULT NULL COMMENT '技能属性名称',
  `tech_value_id` varchar(255) DEFAULT NULL COMMENT '技能属性值ID',
  `value` varchar(255) DEFAULT NULL COMMENT '技能属性值',
  `status` tinyint(4) DEFAULT NULL,
  `rank` varchar(255) DEFAULT NULL COMMENT '排名',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户技能表';

-- ----------------------------
-- Records of t_user_tech_info
-- ----------------------------
