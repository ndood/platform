CREATE TABLE `t_user_body_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT '' COMMENT '用户认证的名字',
  `card_no` varchar(50) DEFAULT NULL COMMENT '用户身份证号',
  `card_front_url` varchar(255) DEFAULT NULL COMMENT '用户身份证正面照片',
  `card_back_url` varchar(255) DEFAULT NULL COMMENT '用户身份证背面照片',
  `auth_status` tinyint(4) DEFAULT '0' COMMENT '认证状态  0 未认证  1已通过  2未通过',
  `remarks` varchar(1000) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='用户身份认证信息表';

