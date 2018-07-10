-- t_tag表增加一个字段：category_id
ALTER TABLE `t_tag` ADD COLUMN `category_id` int(11) DEFAULT NULL COMMENT '分类ID' after `pid`;