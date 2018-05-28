-- ----------------------------
-- Table column add for t_user_tech_auth
-- 技能认证表添加好友认可次数
-- ----------------------------
ALTER TABLE `t_user_tech_auth` ADD COLUMN approve_count INT ( 11 ) NULL DEFAULT 0 COMMENT '好友认可次数';
UPDATE t_user_tech_auth SET approve_count = 0;



-- ----------------------------
-- Table structure for t_approve
-- 增加好友认可记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_approve`;
CREATE TABLE `t_approve`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `tech_owner_id` int(11) NULL DEFAULT NULL COMMENT '技能拥有者id',
  `tech_auth_id` int(11) NULL DEFAULT NULL COMMENT '认可的技能',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '点击认可的用户的id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '生成时间',
  PRIMARY KEY (`id`) ,
  INDEX `tech_auth_id`(`tech_auth_id`) USING BTREE,
  CONSTRAINT `tech_auth_id` FOREIGN KEY (`tech_auth_id`) REFERENCES `t_user_tech_auth` (`id`)
) COMMENT = '好友认可记录表';

-- ----------------------------
-- Table structure for t_banner
-- banner后台管理表
-- ----------------------------
DROP TABLE IF EXISTS `t_banner`;
CREATE TABLE `t_banner`  (
  `id` int(2) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `disable` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序id',
  `operator_id` int(11) NULL DEFAULT NULL COMMENT '操作人id',
  `operator_name` varchar(255)  NULL DEFAULT NULL COMMENT '操作人用户名',
  `pic_url` varchar(255) NULL DEFAULT NULL COMMENT '图片url',
  `redirect_type` tinyint(2) NULL DEFAULT NULL COMMENT '跳转类型',
  `redirect_url` varchar(255) NULL DEFAULT NULL COMMENT '跳转地址',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '生成时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) COMMENT = 'banner管理表';


ALTER TABLE `t_user_info_auth` ADD COLUMN is_reject_submit tinyint ( 1 ) NULL DEFAULT 0 COMMENT '是否是驳回提交(1是,0否）'  AFTER `wechat`;


CREATE TABLE `t_user_info_auth_reject` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `user_info_auth_id` int(11) NOT NULL COMMENT '用户信息认证ID',
  `user_info_auth_status` tinyint(1) NOT NULL COMMENT '用户认证状态',
  `reason` varchar(255) NOT NULL COMMENT '原因',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
)  COMMENT='用户认证信息驳回表';