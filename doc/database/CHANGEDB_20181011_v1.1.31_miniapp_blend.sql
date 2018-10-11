/* 石教云 修改banner表，添加banner有效时间端（为null表示永久有效）  */
ALTER TABLE `t_banner` ADD COLUMN `start_time` datetime DEFAULT NULL
COMMENT '开始时间' after `update_time`;
ALTER TABLE `t_banner` ADD COLUMN `end_time` datetime DEFAULT NULL
COMMENT '结束时间' after `start_time`;
