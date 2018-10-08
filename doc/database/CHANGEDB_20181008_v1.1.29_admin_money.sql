-- 修改零钱明细表，新增用户类型
ALTER TABLE `t_money_details` ADD COLUMN `user_type` tinyint(1) DEFAULT '1'
COMMENT '用户类型(1:用户；2：马甲用户)' after `remark`;
update t_money_details set user_type = 1 where ifnull(user_type,0) = 0
