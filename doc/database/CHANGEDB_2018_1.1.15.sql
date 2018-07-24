--t_user_info_auth表增加字段is_reject_submit
ALTER TABLE `t_user_info_auth` ADD COLUMN `is_platform_show1` tinyint(1) DEFAULT '1'
COMMENT '用户信息是否在平台内展示（0：不展示，1：展示）' after `is_reject_submit`;