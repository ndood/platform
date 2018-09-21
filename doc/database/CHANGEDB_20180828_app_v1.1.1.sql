-- 2018年9月7日上线准备sql

ALTER TABLE `t_user_info_auth` ADD COLUMN `im_substitute_id` int(11)  DEFAULT NULL  COMMENT '代聊客服ID';  -- 没有注释


-- 修改推送功能
ALTER TABLE `t_push_msg` ADD COLUMN `title` varchar(255) NOT NULL COMMENT '推送标题' after `touch_time`;
ALTER TABLE `t_push_msg` MODIFY COLUMN `type` tinyint(4) comment '推送类型(1:推送所有用户；2:推送指定用户；3：推送所有陪玩师)';
ALTER TABLE `t_push_msg` MODIFY COLUMN `platform` tinyint(1) comment '平台(1:陪玩;2:开黑;3:H5;45:APP)';
ALTER TABLE `t_push_msg` ADD COLUMN `jump_type` tinyint(1) DEFAULT NULL COMMENT '跳转类型（1：H5；2：内部跳转页(小程序）；3：官方公告；4：聊天室；5：名片页）' after `type`;


-- 用户表添加虚拟货币字段
ALTER TABLE `t_user` ADD COLUMN `virtual_balance` int(11) unsigned DEFAULT '0' COMMENT '虚拟零钱（对应钻石数量）' after `balance`;
ALTER TABLE `t_user` ADD COLUMN `charm` int(11) unsigned DEFAULT '0' COMMENT '魅力值' after `virtual_balance`;

-- 添加陪玩师推荐字段
ALTER TABLE `t_user_info_auth` ADD COLUMN `sort` int(11) DEFAULT NULL COMMENT '推荐位排序字段' after `allow_export`;

-- IM消息同步功能
DROP TABLE IF EXISTS `t_admin_im_log`;
CREATE TABLE `t_admin_im_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_im_id` varchar(255) DEFAULT NULL COMMENT '消息发送者',
  `target_im_id` varchar(255) DEFAULT NULL COMMENT '消息接收者',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `sendTime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `owner_user_id` int(11) DEFAULT NULL COMMENT '谁可以同步这个消息',
  PRIMARY KEY (`id`)
) COMMENT='im消息同步表';


-- 虚拟商品功能
DROP TABLE IF EXISTS `t_virtual_product`;
CREATE TABLE `t_virtual_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `price` int(11) DEFAULT NULL COMMENT '商品价格',
  `type` tinyint(4) DEFAULT NULL COMMENT '1 礼物  2 私照图片组 3 IM解锁图片组 4 IM解锁语音',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `attach_count` int(11) DEFAULT NULL COMMENT '附件数量',
  `object_url` varchar(255) DEFAULT NULL COMMENT '商品图片地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`)
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


-- 修改用户认证表
ALTER TABLE `t_user_info_auth` ADD COLUMN `interests` varchar(64) DEFAULT NULL COMMENT '用户兴趣' after `charm`;
ALTER TABLE `t_user_info_auth` ADD COLUMN `profession` varchar(32) DEFAULT NULL COMMENT '用户职业' after `interests`;
ALTER TABLE `t_user_info_auth` ADD COLUMN `about` varchar(64) DEFAULT NULL COMMENT '用户简介' after `profession`;



--  修改价格多平台显示部分开始 --
-- 销售方式添加显示平台字段
ALTER TABLE `t_sales_mode` ADD COLUMN `platform_show` tinyint(1) DEFAULT NULL COMMENT '显示平台(1小程序，2APP，3都显示)' after `type`;
-- 商品表添加显示平台字段
ALTER TABLE `t_product` ADD COLUMN `platform_show` tinyint(1) DEFAULT NULL COMMENT '冗余sales_mode表(显示平台(1小程序，2APP，3都显示))' after `sales_mode_rank`;

-- 更新数据
UPDATE `t_sales_mode` SET `platform_show` = 3 WHERE `type` = 1;
UPDATE `t_sales_mode` SET `platform_show` = 1 WHERE `type` = 2;


--  添加管理员im账号
ALTER TABLE `t_admin` ADD COLUMN `im_id` varchar(128) DEFAULT NULL COMMENT 'IM账号' after `status`;
ALTER TABLE `t_admin` ADD COLUMN `im_pwd` varchar(128) DEFAULT NULL COMMENT 'IM密码' after `im_id`;



UPDATE `t_product` pro SET `platform_show` = (SELECT `platform_show` FROM `t_sales_mode` sm WHERE pro.sales_mode_id = sm.id) ;


-- ----2018年9月7日上线sql结束--------









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




-- 修改动态表的技能id为商品id
alter table t_dynamic change  column tech_info_id product_id int(11);


-- 修改动态表，添加是否
ALTER TABLE `t_dynamic_file` ADD COLUMN `width` int(11) DEFAULT '0'
COMMENT '图片/视频宽度' after `play_count`;
ALTER TABLE `t_dynamic_file` ADD COLUMN `height` int(11) DEFAULT '0'
COMMENT '图片/视频高度' after `width`;
ALTER TABLE `t_dynamic_file` ADD COLUMN `duration` int(11) DEFAULT '0'
COMMENT '视频时长（单位秒）' after `height`;


drop table if exists t_dynamic_push_msg;

/*==============================================================*/
/* Table: 动态push消息推送记录表                                */
/*==============================================================*/
create table t_dynamic_push_msg
(
   id                   int(11) not null auto_increment,
   dynamic_id           int(11) comment '被关注用户id',
   from_user_id         int(11) comment 'push消息发送用户id',
   from_user_nickname   varchar(64) comment 'push消息发送用户昵称',
   from_user_head_url   varchar(512) comment 'push消息发送用户头像url',
   to_user_id           int(11) comment 'push消息接收用户id',
   push_type            tinyint(1) comment 'push消息类型（1：点赞；2：评论；3打赏）',
   push_content         varchar(256) comment 'push消息内容',
   push_extras          varchar(512) comment 'push消息扩展内容',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   is_del               tinyint(1) comment '状态（0：有效；1：无效）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态Push消息推送表';

/* 修改banner表，添加平台属性（1：属于小程序；2：app）  */
ALTER TABLE `t_banner` ADD COLUMN `platform_type` tinyint(1) DEFAULT '1'
COMMENT 'banner所属平台(1:小程序;2:app)' after `operator_name`;
update `t_banner` set platform_type = 1 where ifnull(platform_type,1) = 1







-- 王彬优惠券表修改
ALTER TABLE `t_coupon_group` ADD COLUMN `category_id`  int(11) NOT NULL DEFAULT 1 COMMENT '限品类(1则为陪玩全品类,10为游戏全品类,11为娱乐全品类)' AFTER `is_new_user`;
ALTER TABLE `t_coupon_group` ADD COLUMN `type`  tinyint(1) NOT NULL DEFAULT 1 COMMENT '类型(1满减，2折扣)' AFTER `category_id`;
ALTER TABLE `t_coupon_group` ADD COLUMN `full_reduction`  decimal(11,2) NOT NULL DEFAULT 0  COMMENT '多少金额可用' AFTER `type`;

ALTER TABLE `t_coupon` ADD COLUMN `category_id`  int(11) NOT NULL DEFAULT 1 COMMENT '限品类(1则为陪玩全品类,10为游戏全品类,11为娱乐全品类)' AFTER `is_new_user`;
ALTER TABLE `t_coupon` ADD COLUMN `type`   tinyint(1) NOT NULL DEFAULT 1 COMMENT '类型(1满减，2折扣)' AFTER `category_id`;
ALTER TABLE `t_coupon` ADD COLUMN `full_reduction`  decimal(11,2) DEFAULT 0 NOT NULL COMMENT '多少金额可用' AFTER `type`;

ALTER TABLE `t_coupon` ADD COLUMN `category_name`  varchar(255) NULL COMMENT '品类名称' AFTER `category_id`;

UPDATE `t_coupon_group` SET `category_id` = 1,`type`=1,`full_reduction`=0;
UPDATE `t_coupon` SET `category_id` = 1,`type`=1,`full_reduction`=0,`category_name`='全品类';

-- 订单表修改
ALTER TABLE `t_order` ADD COLUMN `begin_time`  datetime NOT NULL COMMENT '订单开始时间' AFTER `charges`;
ALTER TABLE `t_order` ADD COLUMN `platform`  tinyint(1) NULL COMMENT '平台(1陪玩，2上分,4ios,5android)' AFTER `type`;



drop table if exists t_price_rule;
/*==============================================================*/
/* Table: 定价规则表                                */
/*==============================================================*/
CREATE TABLE `t_price_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL COMMENT '类型ID',
  `platform_show` tinyint(1) DEFAULT '3' COMMENT '显示平台(1小程序，2APP，3都显示)',
  `order_count` int(11) DEFAULT NULL COMMENT '接单数',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格，同时用于排序',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_del` tinyint(1) DEFAULT NULL COMMENT '删除标记（1：删除；0：正常）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定价规则表';


-- 添加技能接单数
ALTER TABLE `t_user_tech_auth` ADD COLUMN `order_count` int(11) DEFAULT '0' COMMENT '接单数' after `status`;
-- 添加用户最大接单技能价格
ALTER TABLE `t_user_tech_auth` ADD COLUMN `max_price`  decimal(10,2) DEFAULT '0' COMMENT '定价允许最大价格限制' after `order_count`;



-- 添加注册来源（加t_user还是t_user_info_auth）暂定t_user表：
ALTER TABLE `t_user` ADD COLUMN `register_type` tinyint(1) DEFAULT '1' COMMENT '用户注册来源（1：小程序；2：APP）' after `type`;
-- 添加虚拟粉丝数：
ALTER TABLE `t_user_info_auth` ADD COLUMN `virtual_fans_count` int(11) DEFAULT '0' COMMENT '虚拟粉丝数' after `about`;


-- 添加技能虚拟接单数
ALTER TABLE `t_user_tech_auth` ADD COLUMN `virtual_order_count` int(11) DEFAULT '0' COMMENT '虚拟接单数' after `order_count`;
-- 添加技能认证语音
ALTER TABLE `t_user_tech_auth` ADD COLUMN `voice`  varchar(512) DEFAULT NULL COMMENT '语音文件地址' after `grade_pic_url`;
-- 添加技能认证语音时长
ALTER TABLE `t_user_tech_auth` ADD COLUMN `voice_duration`  int(11) DEFAULT NULL COMMENT '语音时长' after `voice`;
-- 添加技能认证来源（小程序、APP）
ALTER TABLE `t_user_tech_auth` ADD COLUMN `resource_type`  tinyint(1) DEFAULT '1' COMMENT '技能认证来源（1：小程序；2：APP）' after `voice_duration`;
-- 添加技能平均得分
ALTER TABLE `t_user_tech_auth` ADD COLUMN `score_avg` decimal(2,1) DEFAULT NULL COMMENT '技能评分' after `resource_type`;

ALTER TABLE `t_user_tech_auth` ADD COLUMN `is_main` tinyint(1) DEFAULT '0' COMMENT '是否是主要技能' after `is_activate`;


-- 分类表新增：
ALTER TABLE `t_category` ADD COLUMN `example_pic_url`  varchar(512) DEFAULT NULL COMMENT '示例图片url地址' after `charges`;
ALTER TABLE `t_category` ADD COLUMN `example_about`  varchar(128) DEFAULT NULL COMMENT '示例说明' after `example_pic_url`;



-- 用户评论
ALTER TABLE `t_user_comment` ADD COLUMN `category_id`  int(11) DEFAULT NULL COMMENT '游戏分类' after `order_no`;
ALTER TABLE `t_user_comment` ADD COLUMN `tech_auth_id`  int(11) DEFAULT NULL COMMENT '游戏技能ID' after `category_id`;

CREATE TABLE `t_user_comment_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) NOT NULL COMMENT '评论ID',
  `user_id` int(11) NOT NULL COMMENT '陪玩师用户ID',
  `tech_auth_id` int(11) NOT NULL COMMENT '技能ID',
  `tag_id` int(11) NOT NULL COMMENT '标签ID',
  `tag_name` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) COMMENT='用户评论标签表';


CREATE TABLE `t_appstore_pay_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transaction_id` varchar(128) NOT NULL,
  `original_transaction_id` varchar(128) NOT NULL,
  `product_id` varchar(128) NOT NULL,
  `quantity` int(11) NOT NULL,
  `purchase_date` varchar(128) NOT NULL,
  `order_no` varchar(128) DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `transaction_id` (`transaction_id`)
) COMMENT='苹果内购支付流水表';


create table t_server_comment
(
   id                   int(11) not null auto_increment,
   order_no             varchar(128) comment '订单编号',
   user_id              int(11) comment '下单用户id（被评论用户id）',
   server_user_id       int(11) comment '陪玩师id（评论用户id）',
   score                int(1) comment '评分(几颗星1-5分)',
   score_avg            decimal(2,1) comment '平均得星数(不超过5.0,1位小数)',
   content              varchar(128) comment '评论内容（不超过100个字）',
   create_time          datetime comment '评论创建时间',
   update_time          datetime comment '修改时间',
   primary key (id)
)COMMENT='陪玩师评价用户表';

ALTER TABLE `t_user` ADD COLUMN `server_score_avg`  decimal(2,1) DEFAULT NULL COMMENT '陪玩师评价平均分' after `score_avg`;

CREATE TABLE `t_assign_order_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `enable` tinyint(1) NOT NULL COMMENT '激活',
  `begin_time` varchar(255) DEFAULT NULL COMMENT '设置开始时间',
  `end_time` varchar(255) DEFAULT NULL COMMENT '设置结束时间',
  `week_day_bins` int(11) DEFAULT NULL COMMENT '每周那天接单',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`) USING BTREE
)  COMMENT='派单设置';



-- 添加用户职业表
create table t_user_profession
(
   id                   int(11) not null auto_increment,
   name                 varchar(32) comment '职业名称',
   sort                 int(11) comment '排序号',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   is_del               tinyint(1) comment '删除标志（1：删除；0：未删除）',
   primary key (id)
)comment '用户职业表';