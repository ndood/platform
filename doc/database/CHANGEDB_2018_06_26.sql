CREATE TABLE `t_price_factor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `factor` decimal(11,2) NOT NULL COMMENT '价格系数',
  `category_ids` varchar(128) DEFAULT NULL COMMENT '类别ID列表',
  `admin_id` int(11) NOT NULL,
  `admin_name` varchar(128) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='价格系数表';


CREATE TABLE `t_pilot_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(128) NOT NULL,
  `name` varchar(255) NOT NULL,
  `coupon_no` varchar(128) DEFAULT NULL,
  `category_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `service_user_id` int(11) NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `product_num` int(11) NOT NULL COMMENT '商品数量',
  `product_price` decimal(11,2) NOT NULL COMMENT '商品原始价格',
  `pilot_product_price` decimal(11,2) NOT NULL COMMENT '领航商品价格',
  `total_money` decimal(11,2) NOT NULL COMMENT '平台订单总额',
  `pilot_total_money` decimal(11,2) NOT NULL COMMENT '领航订单总额',
  `spread_money` decimal(11,2) NOT NULL,
  `is_complete` bit(1) NOT NULL DEFAULT b'0' COMMENT '该订单是否完成',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`) USING BTREE
) COMMENT='领航订单表';

--添加支付陪玩师金额字段
ALTER TABLE `t_order`
ADD COLUMN `server_money`  decimal(11,2) NULL COMMENT '支付陪玩师金额' AFTER `actual_money`;

ALTER TABLE `t_pilot_order`
ADD COLUMN `factor`  decimal(11,2) NULL DEFAULT NULL COMMENT '费率' AFTER `spread_money`

--同步之前打手收益为空的数据
UPDATE `t_order` set `server_money` = `total_money` - `commission_money`