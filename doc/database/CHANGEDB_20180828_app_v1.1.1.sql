-- 举报表
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

-- 举报文件表
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
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_user_id` int(11) DEFAULT NULL COMMENT '发起关注操作用户id',
  `to_user_id` int(11) DEFAULT NULL COMMENT '目标用户id',
  `is_attention` tinyint(4) DEFAULT '1' COMMENT '是否关注（1：是；0：否）',
  `is_black` tinyint(4) DEFAULT '0' COMMENT '是否黑名单（1：是；0：否）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（1：有效；0：无效）',
  PRIMARY KEY (`id`)
) COMMENT='用户朋友关系表';


DROP TABLE IF EXISTS `t_admin_im_log`;
CREATE TABLE `t_admin_im_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_im_id` varchar(255) DEFAULT NULL,
  `target_im_id` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `sendTime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `owner_user_id` int(11) DEFAULT NULL ,
  PRIMARY KEY (`id`)
) COMMENT='im消息同步表';

ALTER TABLE `t_user_info_auth` ADD COLUMN `im_substitute_id` int(11)  DEFAULT NULL;


-- 添加title（推送标题）字段
ALTER TABLE `t_push_msg` ADD COLUMN `title` varchar(255) NOT NULL COMMENT '推送标题' after `touch_time`;

ALTER TABLE `t_user` ADD COLUMN `virtual_balance` int(11) unsigned DEFAULT '0' COMMENT '虚拟零钱（对应钻石数量）' after `balance`;
ALTER TABLE `t_user` ADD COLUMN `charm` int(11) unsigned DEFAULT '0' COMMENT '魅力值' after `virtual_balance`;
ALTER TABLE `t_user_info_auth` ADD COLUMN `sort` int(11) DEFAULT NULL COMMENT '推荐位排序字段' after `allow_export`;


DROP TABLE IF EXISTS `t_virtual_product`;
CREATE TABLE `t_virtual_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `price` int(11) DEFAULT NULL COMMENT '商品价格',
  `type` tinyint(4) DEFAULT NULL COMMENT '1 礼物  2 私照图片组 3 IM解锁图片组 4 IM解锁语音',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `attach_count` int(11) DEFAULT NULL COMMENT '附件数量',
  `object_url` varchar(255) DEFAULT NULL COMMENT '商品图片地址',
  `remark` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记(0：未删除；1：已删除）',
  PRIMARY KEY (`id`)
)  COMMENT='虚拟商品表';


CREATE TABLE `t_virtual_product_attach` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '文件归属人的ID',
  `virtual_product_id` int(11) DEFAULT NULL COMMENT '虚拟商品ID',
  `url` varchar(255) DEFAULT NULL COMMENT '文件对应的URL地址',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) COMMENT='虚拟商品附件表';


DROP TABLE IF EXISTS `t_virtual_product_order`;
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
) COMMENT='虚拟商品订单表';

-- t_virtual_details
CREATE TABLE `t_virtual_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `relevant_no` varchar(128) DEFAULT NULL COMMENT '关联编号',
  `sum` int(11) DEFAULT NULL COMMENT '剩余虚拟币或者魅力值余额',
  `money` int(11) DEFAULT NULL COMMENT '虚拟币或魅力值的增加和消费记录',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型（1：虚拟币；2：魅力值）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='虚拟币和魅力值详情流水表';


ALTER TABLE `t_push_msg` MODIFY COLUMN `type` tinyint(4) comment '推送类型(1:推送所有用户；2:推送指定用户；3：推送所有陪玩师)';
ALTER TABLE `t_push_msg` MODIFY COLUMN `platform` tinyint(1) comment '平台(1:陪玩;2:开黑;3:H5;45:APP)';
ALTER TABLE `t_push_msg` ADD COLUMN `jump_type` tinyint(1) DEFAULT NULL COMMENT '跳转类型（1：H5；2：内部跳转页(小程序）；3：官方公告；4：聊天室；5：名片页）' after `type`;
ALTER TABLE `t_virtual_details` ADD COLUMN `relevant_no` varchar(128) DEFAULT NULL COMMENT '关联编号' after `user_id`;


-- 修改用户认证表
ALTER TABLE `t_user_info_auth` ADD COLUMN `interests` varchar(64) DEFAULT NULL
COMMENT '用户兴趣' after `charm`;
ALTER TABLE `t_user_info_auth` ADD COLUMN `profession` varchar(32) DEFAULT NULL
COMMENT '用户职业' after `interests`;
ALTER TABLE `t_user_info_auth` ADD COLUMN `about` varchar(64) DEFAULT NULL
COMMENT '用户简介' after `profession`;



drop table if exists t_dynamic;
/*==============================================================*/
/* Table: 动态表                                             */
/*==============================================================*/
create table t_dynamic
(
   id                   int(11) not null auto_increment comment '动态id',
   user_id              int(11) comment '动态发布用户id',
   tech_info_id         int(11) comment '技能id',
   content              varchar(1024) comment '动态内容',
   type                 tinyint(1) comment '动态类型(0：文字；1：图片；2：视频)',
   city_code            varchar(32) comment '城市编码（用于查询附近的动态）',
   city_name            varchar(64) comment '城市名称',
   geohash              varchar(32) comment '地理位置hash（用于查询附近的动态）',
   geohash_short        varchar(32) comment '地理位置hash（用于查询附近的动态，去掉geohash后两位）',
   lon                  double(10,5) comment '经度（用于计算距离）',
   lat                  double(10,5) comment '纬度（用于计算距离）',
   is_top               tinyint(1) default 0 comment '是否置顶（1：是；0：否）',
   is_hot               tinyint(1) default 0 comment '是否热门（1：是；0：否，预留）',
   rewards              int(11) default 0 comment '打赏次数',
   likes                int(11) default 0 comment '点赞次数',
   comments             int(11) default 0 comment '评论次数',
   reports              int(11) comment '举报次数（预留）',
   clicks               int(11) comment '点击次数（预留）',
   create_time          datetime comment '创建时间',
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间（预留，暂不确定是否有修改功能）',
   status               tinyint(1) default 1 comment '动态状态（1：有效；0：无效）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态表';


drop table if exists t_dynamic_file;
/*==============================================================*/
/* Table: 动态文件表                                        */
/*==============================================================*/
create table t_dynamic_file
(
   id                   int(11) not null auto_increment,
   dynamic_id           int(11) comment '动态id',
   url                  varchar(512) comment '文件链接地址',
   type                 tinyint(1) default 1 comment '文件类型(1：图片；2：视频，由于同一条动态不能同时选图片和视频，因此将此属性放到主表中)',
   play_count           int(11) comment '播放次数（视频才会有）',
   create_time          datetime comment '创建时间（预留）',
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   status               tinyint(1) comment '状态（1：有效；0：无效）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态文件表';

drop table if exists t_dynamic_like;
/*==============================================================*/
/* Table: 动态点赞表                                        */
/*==============================================================*/
create table t_dynamic_like
(
   id                   int(11) not null auto_increment comment '评论id',
   dynamic_id           int(11) comment '动态id',
   from_user_id         int(11) comment '点赞用户id',
   from_user_head_url   varchar(512) comment '点赞用户头像URL（冗余字段，提高查询效率）',
   from_user_nickname   varchar(32) comment '点赞用户昵称（冗余字段，提高查询效率）',
   create_time          datetime comment '创建时间',
   status               tinyint(1) default 1 comment '状态（1：有效；0：取消赞）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';


drop table if exists t_reward;

/*==============================================================*/
/* Table: 打赏记录表                                              */
/*==============================================================*/
create table t_reward
(
   id                   int(11) not null auto_increment,
   resource_id          int(11) comment '来源id',
   resource_type        smallint comment '来源类型（1：动态打赏；）',
   gift_id              int(11) comment '礼物id',
   gift_url              varchar(512) comment '礼物图标（冗余字段，提高查询效率）',
   from_user_id         int(11) comment '给打赏用户id',
   from_user_head_url   varchar(512) comment '给打赏用户头像URL（冗余字段，提高查询效率）',
   from_user_nickname   varchar(32) comment '给打赏用户昵称（冗余字段，提高查询效率）',
   from_user_gender     tinyint(1) comment '给打赏用户性别(默认0：不公开；1：男；2：女)',
   to_user_id           int(11) comment '获得打赏用户id(预留)',
   to_user_head_url     varchar(512) comment '获得打赏用户头像URL（冗余字段，提高查询效率）',
   to_user_nickname     varchar(32) comment '获得打赏用户昵称（冗余字段，提高查询效率）',
   to_user_gender       tinyint(1) comment '获得打赏用户性别(默认0：不公开；1：男；2：女)',
   create_time          datetime comment '创建时间',
   status               tinyint(1) comment '状态（1：有效；0：无效）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打赏记录表';

drop table if exists t_dynamic_comment;
/*==============================================================*/
/* Table: 动态评论表                               */
/*==============================================================*/
create table t_dynamic_comment
(
   id                   int(11) not null auto_increment comment 'id',
   dynamic_id           int(11) comment '动态id(用户查询评论的所有回复信息)',
   comment_id           int(11) comment '目标id（当reply_type=1时为评论id，回复的父id）',
   comment_type         smallint comment '类型（1：评论动态；2：评论动态的评论）',
   content              varchar(1024) comment '评论的内容',
   from_user_id         int(11) comment '用户id',
   from_user_head_url   varchar(512) comment '用户头像URL（冗余字段，提高查询效率）',
   from_user_nickname   varchar(32) comment '用户昵称（冗余字段，提高查询效率）',
   from_user_gender     tinyint(1) comment '用户性别(默认0：不公开；1：男；2：女)',
   to_user_id           int(11) comment '目标用户id',
   to_user_head_url     varchar(512) comment '目标用户头像URL（冗余字段，提高查询效率）',
   to_user_nickname     varchar(32) comment '目标用户昵称（冗余字段，提高查询效率）',
   to_user_gender       tinyint(1) comment '目标用户性别(默认0：不公开；1：男；2：女)',
   create_time          datetime comment '创建时间',
   status               tinyint(1) default 1 comment '状态（1：有效；0：无效）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论表';

drop table if exists t_access_log;
/*==============================================================*/
/* Table: 访问日志记录表                                        */
/*==============================================================*/
create table t_access_log
(
   id                   int(11) not null auto_increment comment 'id自增',
   from_user_id         int(11) comment '访问者id',
   to_user_id           int(11) comment '被访问者id',
   menus_name           varchar(512) comment '访问菜单名称逗号间隔',
   count                int default 1 comment '访问次数',
   city_code            varchar(16) comment '城市编码',
   city_name            varchar(16) comment '城市名称',
   create_time          datetime comment '创建时间',
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间（同一个访问者和被访问者只会有一条记录，通过update_time来查，详细列表通过详情表来查）',
   status               tinyint(1) comment '状态（1：有效；0：无效）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志记录表';


drop table if exists t_access_log_detail;
/*==============================================================*/
/* Table: 访问日志详情表                                        */
/*==============================================================*/
create table t_access_log_detail
(
   id                   int(11) not null auto_increment comment 'id自增',
   access_log_id        int(11) comment '访问日志id',
   menus_name           varchar(512) comment '访问过的菜单名称逗号间隔',
   city_code            varchar(16) comment '城市编码',
   city_name            varchar(16) comment '城市名称',
   create_time          datetime comment '创建时间',
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   status               tinyint(1) comment '状态（1：有效；0：无效）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志详情表';



drop table if exists t_user_interests;

/*==============================================================*/
/* Table: 用户兴趣表                                      */
/*==============================================================*/
create table t_user_interests
(
   id                   int(11) not null auto_increment,
   name                 varchar(32) comment '兴趣名称',
   sort                 int comment '排序号',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   status               tinyint(1) comment '数据状态(1：有效；0：无效)',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣表';

-- 修改打赏记录表：添加钻石数冗余字段
ALTER TABLE `t_reward` ADD COLUMN `gift_price` int(11) DEFAULT '0'
COMMENT '礼物钻石数' after `gift_url`;

-- 修改动态表，添加是否
ALTER TABLE `t_dynamic` ADD COLUMN `order_count` int(11) DEFAULT '0'
COMMENT '下单数' after `is_hot`;
ALTER TABLE `t_dynamic` ADD COLUMN `operator_id` int(11) DEFAULT '0'
COMMENT '后端操作者id' after `order_count`;
ALTER TABLE `t_dynamic` ADD COLUMN `operator_name` varchar(64) DEFAULT NULL
COMMENT '后端操作者名称' after `operator_id`;


--  修改价格多平台显示部分开始 --
-- 销售方式添加显示平台字段
ALTER TABLE `t_sales_mode` ADD COLUMN `platform_show` tinyint(1) DEFAULT NULL COMMENT '显示平台(1小程序，2APP，3都显示)' after `type`;
-- 商品表添加显示平台字段
ALTER TABLE `t_product` ADD COLUMN `platform_show` tinyint(1) DEFAULT NULL COMMENT '冗余sales_mode表(显示平台(1小程序，2APP，3都显示))' after `sales_mode_rank`;

--
UPDATE `t_sales_mode` SET `platform_show` = 3 WHERE `type` = 1;
UPDATE `t_sales_mode` SET `platform_show` = 1 WHERE `type` = 2;

UPDATE `t_product` pro SET `platform_show` = (SELECT `platform_show` FROM `t_sales_mode` sm WHERE pro.sales_mode_id = sm.id) ;

--  修改价格多平台显示部分结束 --


-- 修改动态表的技能id为商品id
alter table t_dynamic change  column tech_info_id product_id int(11);

ALTER TABLE `t_admin` ADD COLUMN `im_id` varchar(128) DEFAULT NULL COMMENT 'IM账号' after `status`;
ALTER TABLE `t_admin` ADD COLUMN `im_pwd` varchar(128) DEFAULT NULL COMMENT 'IM密码' after `im_id`;
