-- t_pilot_order表增加四个字段：admin_id、admin_name、update_time、admin_remark
ALTER TABLE `t_pilot_order` ADD COLUMN `admin_remark` varchar(512) DEFAULT NULL COMMENT '管理员备注';
ALTER TABLE `t_pilot_order` ADD COLUMN `admin_id` int(11) NOT NULL COMMENT '管理员ID';
ALTER TABLE `t_pilot_order` ADD COLUMN `admin_name` varchar(256) NOT NULL COMMENT '管理员名称';
ALTER TABLE `t_pilot_order` ADD COLUMN `update_time` datetime DEFAULT NULL;

-- ----------------------------
-- Table structure for t_pilot_order_details
-- 领航订单打款流水表
-- ----------------------------
DROP TABLE IF EXISTS `t_pilot_order_details`;
CREATE TABLE `t_pilot_order_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `remark` varchar(256) NOT NULL,
  `money` decimal(11,2) NOT NULL COMMENT '本次金额',
  `sum` decimal(11,2) NOT NULL COMMENT '总金额',
  `admin_id` int(11) NOT NULL COMMENT '管理员ID',
  `admin_name` varchar(256) NOT NULL COMMENT '管理员名称',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) COMMENT='领航订单打款流水表';