ALTER TABLE `t_order`
ADD COLUMN `is_pay_callback`  varchar(255) NULL comment='是否接收过微信支付回调(1:已接收,0:未接收)'  AFTER `is_pay`;


ALTER TABLE `t_order`
ADD COLUMN `charges`  decimal(11,2) NULL DEFAULT NULL COMMENT '佣金比例' AFTER `order_ip`;