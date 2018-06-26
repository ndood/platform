CREATE TABLE `t_price_factor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `factor` decimal(11,2) NOT NULL COMMENT '价格系数',
  `admin_id` int(11) NOT NULL,
  `admin_name` varchar(128) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='价格系数表';