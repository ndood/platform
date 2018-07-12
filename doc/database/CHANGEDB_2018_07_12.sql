-- t_money_details表修改action字段注释
ALTER TABLE `t_money_details` MODIFY COLUMN `action` tinyint(1) NOT NULL
COMMENT '-1提款，1加零钱，2陪玩订单入账，3拒绝提现返款';
-- t_cash_draws表修改cash_status字段注释
ALTER TABLE `t_cash_draws` MODIFY COLUMN `cash_status` int(3) DEFAULT '0'
COMMENT '申请单处理状态（0：未处理，1：已打款，2：已拒绝）';