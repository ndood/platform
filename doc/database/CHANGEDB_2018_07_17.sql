--t_user表增加字段user_score
ALTER TABLE `t_user` ADD COLUMN `user_score` int(11) DEFAULT '0' COMMENT '用户总积分' after `age`

--建表t_user_score_details用户积分详情表
DROP TABLE IF EXISTS `t_user_score_details`;
CREATE TABLE `t_user_score_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `score` int(5) NOT NULL COMMENT '积分变动',
  `description` varchar(64) DEFAULT NULL COMMENT '积分变动描述',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '记录生成时间',
  PRIMARY KEY (`id`)
)  COMMENT='用户积分详情表';


DROP TABLE IF EXISTS `t_order_event`;
CREATE TABLE `t_order_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `order_status` int(11) DEFAULT NULL COMMENT '协商前订单状态',
  `apply_id` int(11) NOT NULL COMMENT '申请人ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `service_user_id` int(11) NOT NULL COMMENT '陪玩师ID',
  `type` tinyint(4) NOT NULL COMMENT '1:仲裁，2:验收,3:协商',
  `refund_money` decimal(11,2) DEFAULT NULL COMMENT '退款',
  `create_time` datetime NOT NULL,
  `is_del` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='订单事件表';

-- ----------------------------
-- Table structure for t_order_share_profit
-- ----------------------------
DROP TABLE IF EXISTS `t_order_share_profit`;
CREATE TABLE `t_order_share_profit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(128) NOT NULL COMMENT '订单号',
  `server_money` decimal(11,2) DEFAULT NULL COMMENT '陪玩师金额',
  `commission_money` decimal(11,2) DEFAULT NULL COMMENT '平台收入金额',
  `user_money` decimal(11,2) DEFAULT NULL COMMENT '退款金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='订单分润表';


DROP TABLE IF EXISTS `t_order_status_details`;
CREATE TABLE `t_order_status_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(128) NOT NULL COMMENT '订单号',
  `order_status` int(11) NOT NULL COMMENT '订单状态',
  `trigger_time` datetime NOT NULL COMMENT '订单触发时间',
  `count_down_minute` int(11) DEFAULT NULL COMMENT '倒计时时间',
  `is_valid` tinyint(1) DEFAULT NULL COMMENT '是否有效',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) COMMENT='订单状态详情表';


ALTER TABLE `t_order`
ADD COLUMN `is_pay_callback`  tinyint(1) NULL COMMENT '是否接收过微信支付回调(1:已接收,0:未接收)'  AFTER `is_pay`;

ALTER TABLE `t_order`
ADD COLUMN `charges`  decimal(11,2) NULL DEFAULT NULL COMMENT '佣金比例' AFTER `order_ip`;

ALTER TABLE `t_order_deal`
ADD COLUMN `order_event_id`  int(11) NULL DEFAULT NULL COMMENT '订单事件ID' AFTER `order_no`;

ALTER TABLE `t_order_deal`
ADD COLUMN `title` varchar(255) NULL DEFAULT NULL COMMENT '订单留言标题' AFTER `order_event_id`;

-- 建表t_arbitration_details（仲裁结果流水表）
DROP TABLE IF EXISTS `t_arbitration_details`;
CREATE TABLE `t_arbitration_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `service_user_id` int(11) NOT NULL COMMENT '陪玩师ID',
  `refund_user_money` decimal(11,2) DEFAULT NULL COMMENT '退款给用户的金额',
  `refund_service_user_money` decimal(11,2) DEFAULT NULL COMMENT '退款给陪玩师的金额',
  `commission_money` decimal(11,2) DEFAULT NULL COMMENT '平台收入金额',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='仲裁结果流水表';

-- 新增临时表
DROP TABLE IF EXISTS `t_user_info_auth_file_temp`;
CREATE TABLE `t_user_info_auth_file_temp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `info_auth_id` int(11) DEFAULT NULL COMMENT '关联信息认证ID',
  `name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1写真图片,2声音,3主图)',
  `url` varchar(255) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `ext` varchar(255) DEFAULT NULL COMMENT '扩展名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='信息认证文件临时表（图片、声音）';


--todo
t_order表 isPay和isPayCallback两个状态同步
--todo
t_order_share_profit 表金额和t_order表金额同步

