-- t_tag表增加一个字段：category_id
ALTER TABLE `t_tag` ADD COLUMN `category_id` int(11) DEFAULT NULL COMMENT '分类ID' after `pid`;

-- 修改同步之前游戏类别标签
UPDATE `t_tag` ta SET `category_id` = (SELECT `id` FROM `t_category` as ca  WHERE ca.`tag_id` = ta.`id` AND `category_id` IS NOT NULL)


-- t_order表
ALTER TABLE `t_order` ADD COLUMN `contact_info` varchar(64) DEFAULT NULL COMMENT '联系方式' after `status`;
ALTER TABLE `t_order` ADD COLUMN `contact_type` tinyint(1) unsigned DEFAULT NULL
COMMENT '联系方式类型(1：手机号，2：QQ号，3：微信号)' after `status`;



