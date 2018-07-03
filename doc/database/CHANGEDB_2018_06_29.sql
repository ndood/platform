--cdk批次增加备注字段
ALTER TABLE `t_cdk_group` ADD COLUMN `remark` VARCHAR(255) DEFAULT NULL COMMENT "备注" AFTER `channel_name`;
--user表增加如下字段
ALTER TABLE `t_user` ADD COLUMN `regist_ip` VARCHAR(128) DEFAULT NULL COMMENT "注册ip" AFTER `source_id`;
ALTER TABLE `t_user` ADD COLUMN `login_ip` VARCHAR(128) DEFAULT NULL COMMENT "最后登录ip" AFTER `regist_ip`;
ALTER TABLE `t_user` ADD COLUMN `login_time` datetime COMMENT "最后登录时间" AFTER `login_ip`;

ALTER TABLE `t_category` ADD COLUMN `index_icon` VARCHAR(255) DEFAULT NULL COMMENT "首页ICON" AFTER `icon`;


-- ----------------------------
-- Table structure for t_advice
-- 增加意见反馈表
-- ----------------------------
DROP TABLE IF EXISTS `t_advice`;
CREATE TABLE `t_advice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `nickname` varchar(128) NULL DEFAULT NULL COMMENT '昵称',
  `contact` varchar(255) NULL DEFAULT NULL COMMENT '联系方式',
  `content` varchar(302) NOT NULL DEFAULT '' COMMENT '建议内容',
  `status` tinyint(2) NULL DEFAULT NULL COMMENT '状态(0待处理,1标记,2已处理)',
  `admin_id` int(11) NULL DEFAULT NULL COMMENT '管理员id',
  `admin_name` varchar(128) NULL DEFAULT NULL COMMENT '管理员名字',
  `remark` varchar(255) NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL COMMENT '生成(反馈)时间',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) COMMENT = '意见反馈表';

-- ----------------------------
-- Table structure for t_advice_file
-- 意见图片文件关联表
-- ----------------------------
DROP TABLE IF EXISTS `t_advice_file`;
CREATE TABLE `t_advice_file`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `advice_id` int(11) NOT NULL COMMENT '关联的建议id',
  `url` varchar(128) NOT NULL COMMENT '图片地址',
  `create_time` datetime(0) NOT NULL COMMENT '生成时间',
  PRIMARY KEY (`id`) ,
  INDEX `advice_id_fk`(`advice_id`) ,
  CONSTRAINT `advice_id_fk` FOREIGN KEY (`advice_id`) REFERENCES `t_advice` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) COMMENT = '意见反馈图片文件表';