CREATE TABLE `t_sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) DEFAULT NULL COMMENT '配置值类型(1:boolean,2:string,3:json)',
  `name` varchar(255) DEFAULT NULL COMMENT '配置名',
  `value` varchar(255) DEFAULT NULL COMMENT '配置值',
  `note` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='系统配置表';

-- ----------------------------
-- Records of t_sys_config
-- ----------------------------
INSERT INTO `t_sys_config` VALUES ('1', '1', 'MMCON', 'CLOSE', 'IM开启关闭(CLOSE:全关,ALL:全打开,ANDROID:安卓打开,IOS:IOS打开)', '2018-05-14 15:07:46', '2018-05-14 15:07:48');
INSERT INTO `t_sys_config` VALUES ('2', '1', 'PAYCON', 'CLOSE', '支付开启关闭(CLOSE:全关,ALL:全打开,ANDROID:安卓打开,IOS:IOS打开)', '2018-05-14 15:08:16', '2018-05-14 15:08:19');


ALTER TABLE `t_sys_config` DROP COLUMN `type`;