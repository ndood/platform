--t_user表增加字段user_score
ALTER TABLE `t_user` ADD COLUMN `user_score` int(11) DEFAULT '0' COMMENT '用户总积分' after `age`

--建表t_user_score_details用户积分详情表
DROP TABLE IF EXISTS `t_user_score_details`;
CREATE TABLE `t_user_score_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `score` int(5) NOT NULL COMMENT '积分变动',
  `description` varchar(64) DEFAULT NULL COMMENT '积分变动描述',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '记录生成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='用户积分详情表';









--todo
t_order表 isPay和isPayCallback两个状态同步
--todo
t_order_share_profit 表金额和t_order表金额同步

