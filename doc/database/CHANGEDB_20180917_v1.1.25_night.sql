CREATE TABLE `t_order_admin_remark` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT '订单号',
  `agent_admin_id` int(11) DEFAULT NULL COMMENT '处理的运营ID',
  `agent_admin_name` varchar(255) DEFAULT NULL COMMENT '处理的运营名字',
  `remark` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime DEFAULT NULL COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
  PRIMARY KEY (`id`)
) COMMENT='运营订单备注表';

ALTER TABLE `t_virtual_pay_order` MODIFY COLUMN `pay_path` tinyint(1) DEFAULT NULL
COMMENT '充值路径(1：开黑陪玩；2：开黑上分；3：微信公众号； 4：IOS； 5：ANDROID； 45:APP(android+ios))';


ALTER TABLE `t_user_info_auth` ADD COLUMN `auto_say_hello` VARCHAR(500) COMMENT '自动问好' after `sort`;

ALTER TABLE `t_user_info_auth` ADD COLUMN `open_substitute_im` tinyint(1) COMMENT '陪玩师代聊开关' after `im_substitute_id`;

ALTER TABLE `t_user_info_auth` ADD COLUMN `vest_flag` tinyint(1) DEFAULT '0' COMMENT '是否马甲（0：否；1：是）' after `auto_say_hello`;

CREATE TABLE `t_user_night_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `category_id` int(11) DEFAULT NULL COMMENT '游戏ID',
  `sales_mode_id` int(11) DEFAULT NULL COMMENT '销售单位id',
  `type` tinyint(1) DEFAULT NULL COMMENT '单位种类',
  `name` varchar(128) DEFAULT NULL COMMENT '单位',
  `sort` int(11) DEFAULT NULL COMMENT '推荐位排序字段',
  `admin_id` int(11) DEFAULT NULL COMMENT '操作人id',
  `admin_name` varchar(255) DEFAULT NULL COMMENT '操作人用户名',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记（0：未删除，1：已删除）',
  PRIMARY KEY (`id`)
) COMMENT='午夜场陪玩师信息表';