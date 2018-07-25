-- 需要确认接口删除
-- /order/user/details
-- /server/details


DROP TABLE IF EXISTS `t_grading_price`;
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
DROP TABLE IF EXISTS `t_order_point_product`;
CREATE TABLE `t_order_point_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(128) NOT NULL COMMENT '订单号',
  `point_type` tinyint(4) NOT NULL COMMENT '上分分类',
  `category_id` int(11) NOT NULL COMMENT '游戏分类ID',
  `area_id` int(11) DEFAULT NULL COMMENT '大区ID',
  `grading_price_id` int(11) DEFAULT NULL COMMENT '段位等级',
  `target_grading_price_id` int(11) DEFAULT NULL COMMENT '目标段位等级',
  `category_name` varchar(255) DEFAULT NULL,
  `account_info` varchar(255) DEFAULT NULL COMMENT '账号信息',
  `order_choice` varchar(255) DEFAULT NULL COMMENT '订单选择',
  `price` decimal(10,0) NOT NULL,
  `amount` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='上分订单详情';


DROP TABLE IF EXISTS `t_user_contact`;
CREATE TABLE `t_user_contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `type` int(11) DEFAULT NULL COMMENT '联系方式类型',
  `contact` varchar(255) DEFAULT NULL COMMENT '联系方式',
  `is_default` tinyint(4) DEFAULT NULL COMMENT '是否默认',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='用户联系方式';


ALTER TABLE `t_user` ADD COLUMN `point_open_id` varchar(255) DEFAULT NULL COMMENT '开黑上分openid' after `open_id`;
ALTER TABLE `t_user` ADD COLUMN `public_open_id` varchar(255) DEFAULT NULL COMMENT '微信公众号openid' after `point_open_id`;
ALTER TABLE `t_user` ADD COLUMN `unionid` varchar(255) DEFAULT NULL COMMENT '微信生态唯一标识' after `public_open_id`;