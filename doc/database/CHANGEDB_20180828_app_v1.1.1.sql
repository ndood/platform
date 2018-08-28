--举报表
DROP TABLE IF EXISTS `t_report`;
CREATE TABLE `t_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(11) NOT NULL COMMENT '举报人id',
  `reported_user_id` int(11) NOT NULL COMMENT '被举报人id',
  `content` varchar(128) DEFAULT NULL COMMENT '举报内容',
  `status` tinyint(1) DEFAULT '0' COMMENT '处理状态(0：未处理（默认），1：已处理)',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `admin_id` int(11) DEFAULT NULL COMMENT '管理员id',
  `admin_name` varchar(255) DEFAULT NULL COMMENT '管理员用户名',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='举报表';

--举报文件表
DROP TABLE IF EXISTS `t_report_file`;
CREATE TABLE `t_report_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) NOT NULL COMMENT '举报id',
  `url` varchar(255) DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
)  COMMENT='举报文件表';