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

-- 用户朋友关系表
DROP TABLE IF EXISTS `t_user_friend`;
CREATE TABLE `t_user_friend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_user_id` bigint(20) DEFAULT NULL COMMENT '发起关注操作用户id',
  `to_user_id` bigint(20) DEFAULT NULL COMMENT '目标用户id',
  `is_attention` tinyint(4) DEFAULT '1' COMMENT '是否关注（1：是；0：否）',
  `is_black` tinyint(4) DEFAULT '0' COMMENT '是否黑名单（1：是；0：否）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（1：有效；0：无效）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='用户朋友关系表';


DROP TABLE IF EXISTS `t_admin_im_log`;
CREATE TABLE `t_admin_im_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_im_id` varchar(255) DEFAULT NULL,
  `target_im_id` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `sendTime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

ALTER TABLE `t_user_info_auth` ADD COLUMN `im_substitute_id` int(11)  DEFAULT NULL ;


--添加title（推送标题）字段
ALTER TABLE `t_push_msg` ADD COLUMN `title` varchar(255) NOT NULL COMMENT '推送标题' after `touch_time`;

ALTER TABLE `t_user` ADD COLUMN `virtual_balance` bigint(20) unsigned DEFAULT NULL COMMENT '虚拟零钱（对应钻石数量）' after `balance`;
ALTER TABLE `t_user_info_auth` ADD COLUMN `charm` bigint(20) unsigned DEFAULT NULL COMMENT '魅力值' after `wechat`;
ALTER TABLE `t_user_info_auth` ADD COLUMN `sort` int(11) DEFAULT NULL COMMENT '推荐位排序字段' after `allow_export`;


CREATE TABLE `t_virtual_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `price` int(11) DEFAULT NULL COMMENT '商品价格',
  `type` tinyint(4) DEFAULT NULL COMMENT '1 礼物  2 私照图片组 3 IM解锁图片组 4 IM解锁语音',
  `object_url` varchar(255) DEFAULT NULL COMMENT '商品图片地址',
  `remark` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='虚拟商品表';


CREATE TABLE `t_virtual_product_attach` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '文件归属人的ID',
  `virtual_product_id` int(11) DEFAULT NULL COMMENT '虚拟商品ID',
  `url` varchar(255) DEFAULT NULL COMMENT '文件对应的URL地址',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `t_virtual_product_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_no` varchar(128) NOT NULL COMMENT '订单号',
  `virtual_product_id` int(11) DEFAULT NULL COMMENT '虚拟商品id',
  `price` int(11) DEFAULT NULL COMMENT '虚拟商品价格（对应钻石数量）',
  `from_user_id` int(11) DEFAULT NULL COMMENT '发起人id',
  `target_user_id` int(11) DEFAULT NULL COMMENT '接收人id',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟商品订单表';

CREATE TABLE `t_virtual_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `sum` int(11) DEFAULT NULL COMMENT '剩余虚拟币或者魅力值余额',
  `money` int(11) DEFAULT NULL COMMENT '虚拟币或魅力值的增加和消费记录',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型（1：虚拟币；2：魅力值）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟货币流水表';

