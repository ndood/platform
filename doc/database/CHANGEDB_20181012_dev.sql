-- 虚拟商品订单表增加字段
ALTER TABLE `t_virtual_product_order`
ADD COLUMN `amount` int(11) NOT NULL DEFAULT '1' COMMENT '数量' AFTER `price`;

ALTER TABLE `t_virtual_product_order`
ADD COLUMN `unit_price` int(11) DEFAULT NULL COMMENT '虚拟商品单价' AFTER `amount`;