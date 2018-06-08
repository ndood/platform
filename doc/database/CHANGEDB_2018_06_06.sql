
-- 推送微信消息表
CREATE TABLE `t_push_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL COMMENT '推送类型',
  `page` varchar(255) NOT NULL COMMENT '落地页',
  `push_ids` varchar(2000) DEFAULT NULL COMMENT '推送ID',
  `touch_time` datetime DEFAULT NULL COMMENT '触发时间',
  `content` varchar(255) NOT NULL COMMENT '推送内容',
  `hits` bigint(20) DEFAULT '0' COMMENT '点击数',
  `success_num` int(11) DEFAULT '0' COMMENT '成功数',
  `total_num` int(11) DEFAULT '0' COMMENT '推送总数',
  `is_pushed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推送过了',
  `admin_id` int(11) NOT NULL COMMENT '管理员ID',
  `admin_name` varchar(255) NOT NULL COMMENT '管理员名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)  COMMENT='推送信息表';

CREATE TABLE `t_product_top` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL COMMENT '指定分类',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `mobile` varchar(255) NOT NULL COMMENT '手机号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '上下架(0下架,1上架)',
  `sort` int(11) NOT NULL COMMENT '排序',
  `admin_id` int(11) NOT NULL,
  `admin_name` varchar(255) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_id-user_id` (`category_id`,`user_id`) USING BTREE
)  COMMENT='商品置顶表';
