--t_user_info_auth表增加字段is_reject_submit和source_id
ALTER TABLE `t_user_info_auth` ADD COLUMN `is_platform_show` tinyint(1) DEFAULT '1'
COMMENT '用户信息是否在平台内展示（0：不展示，1：展示）' after `is_reject_submit`;

ALTER TABLE `t_user_info_auth` ADD COLUMN `source_id` int(11) DEFAULT NULL
COMMENT '来源id（对应t_regist_source表的主键id）' after `is_platform_show`;

ALTER TABLE `t_price_factor` ADD COLUMN `source_type` int(1) unsigned zerofill DEFAULT '0'
COMMENT '来源渠道类型（0：领航，1：ChinaJoy）' after `factor`;

-- 用户微信群分享表
DROP TABLE IF EXISTS `t_user_wechat_group_share`;
CREATE TABLE `t_user_wechat_group_share` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `share_status` tinyint(1) unsigned zerofill DEFAULT '0' COMMENT '用户是否已完成分享任务（0：否，1：是）',
  `group_counts` int(11) DEFAULT NULL COMMENT '分享到的微信群数量',
  `group_ids` varchar(512) DEFAULT NULL COMMENT '分享到的微信群的群号（多个以逗号隔开）',
  `coupou_status` tinyint(1) unsigned zerofill DEFAULT '0' COMMENT '优惠券状态（0：发放失败，1：已成功发放）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='用户微信群分享表';