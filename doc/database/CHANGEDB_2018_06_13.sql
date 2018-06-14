

ALTER TABLE `t_order` ADD COLUMN `type`  tinyint(1) NULL AFTER `category_id`;
ALTER TABLE `t_order` ADD COLUMN `channel_id`  int(11) NULL AFTER `user_id`;
ALTER TABLE `t_order` ADD COLUMN `order_ip`  varchar(128) NULL AFTER `actual_money`;



--同步之前的所有订单类型
update `t_order` set type = 1;