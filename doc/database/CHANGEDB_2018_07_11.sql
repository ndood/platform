-- t_pilot_order表修改两个字段：admin_id、admin_name
ALTER TABLE `t_pilot_order` modify COLUMN `admin_name` varchar(256) NULL COMMENT '管理员名称';
ALTER TABLE `t_pilot_order` modify COLUMN `admin_id` int(11) NULL COMMENT '管理员ID';

