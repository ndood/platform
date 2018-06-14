

ALTER TABLE `t_order` ADD COLUMN `type`  tinyint(1) NULL COMMENT '订单类型(1：普通订单，2：集市订单)' AFTER `category_id`;
ALTER TABLE `t_order` ADD COLUMN `channel_id`  int(11) NULL COMMENT '渠道商ID' AFTER `user_id`;
ALTER TABLE `t_order` ADD COLUMN `order_ip`  varchar(128) NULL COMMENT '订单IP' AFTER `actual_money`;
ALTER TABLE `t_order` ADD COLUMN `receiving_time`  varchar(128) NULL COMMENT '接单时间' AFTER `order_ip`;



--同步之前的所有订单类型
update `t_order` set type = 1;