DROP TABLE IF EXISTS `t_sys_router`;
create table t_sys_router
(
   id                   int(11) not null auto_increment comment 'Id',
   pid                  int comment '路由id',
   type                 tinyint(1) comment '路由类型（1：菜单；2：权限；）',
   path                 varchar(256) comment '地址',
   component            varchar(32) comment '组件名',
   name                 varchar(64) comment '名称',
   iconCls              varchar(32) comment '对应图标地址',
   hidden               tinyint(1) default 1 comment '是否显示（1：是；0：否）',
   operator_id          int(11) comment '操作员id',
   operator_name        varchar(32) comment '操作员名称',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   is_del               tinyint(1) comment '删除标识（1：删除；0：未删除）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路由表';

-- 添加排序号
ALTER TABLE `t_sys_router` ADD COLUMN `sort` int(11) DEFAULT NULL
COMMENT '排序号' after `hidden`;



drop table if exists t_sys_role;
create table t_sys_role
(
   id                   int(11) not null auto_increment,
   name                 varchar(32) comment '名称',
   remark               varchar(256) comment '备注信息',
   status               tinyint(1) comment '是否启用（1：是；0：否）',
   operator_id          int(11) comment '操作员id',
   operator_name        varchar(32) comment '操作员名称',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   is_del               tinyint(1) comment '删除标识（1：删除；0：未删除）',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

drop table if exists t_admin_role;
create table t_admin_role
(
   id                   int(11) not null auto_increment,
   admin_id             int(11) comment '用户id',
   role_id              int(11) comment '角色id'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';

drop table if exists t_role_router;
create table t_role_router
(
   id                   int(11) not null auto_increment,
   role_id              int(11) comment '角色id',
   router_id            int(11) comment '路由id'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色路由表';


drop table if exists t_tech_level;
create table t_tech_level
(
   id                   int(11) not null auto_increment,
   name                 varchar(64) comment '技能等级名称',
   sort                 int(11) comment '排序号',
   operator_id          int(11) comment '操作员id',
   operator_name        varchar(32) comment '操作员名称',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   is_del               tinyint(1) comment '删除标志（1：删除；0：未删除）',
   primary key (id)
) comment '技能等级表';


drop table if exists t_conversion_rate;
create table t_conversion_rate
(
   id                   int(11) not null auto_increment,
   name                 varchar(64) comment '统计显示名称',
   time_bucket          varchar(64) comment '统计时间段',
   peoples              int(11) comment '下单人数',
   orders               int(11) comment '下单数',
   amount               decimal(10,2) comment '下单总金额',
   new_peoples          int(11) comment '新人下单人数',
   new_orders           int(11) comment '新人下单数',
   new_amount           decimal(10,2) comment '新人下单总金额',
   new_pays             int(11) comment '新人付款人数',
   new_order_rate       decimal(5,2) comment '新人下单转化率',
   new_pay_rate         decimal(5,2) comment '新人付款转化率',
   repeat_orders        int(11) comment '复购下单人数',
   repeat_pays          int(11) comment '复购付款人数',
   repeat_order_rate    decimal(5,2) comment '复购下单转化率',
   repeat_pay_rate      decimal(5,2) comment '复购付款转化率',
   create_time          datetime comment '统计时间',
   update_time          datetime comment '修改时间',
   primary key (id)
) comment '转换率统计表';


drop table if exists t_conversion_rate_history;
create table t_conversion_rate_history
(
   id                   int(11) not null auto_increment,
   name                 varchar(64) comment '统计显示名称',
   time_bucket          varchar(64) comment '统计时间段',
   peoples              int(11) comment '下单人数',
   orders               int(11) comment '下单数',
   amount               decimal(10,2) comment '下单总金额',
   new_peoples          int(11) comment '新人下单人数',
   new_orders           int(11) comment '新人下单数',
   new_amount           decimal(10,2) comment '新人下单总金额',
   new_pays             int(11) comment '新人付款人数',
   new_order_rate       decimal(5,2) comment '新人下单转化率',
   new_pay_rate         decimal(5,2) comment '新人付款转化率',
   repeat_orders        int(11) comment '复购下单人数',
   repeat_pays          int(11) comment '复购付款人数',
   repeat_order_rate    decimal(5,2) comment '复购下单转化率',
   repeat_pay_rate      decimal(5,2) comment '复购付款转化率',
   create_time          datetime comment '统计时间',
   update_time          datetime comment '修改时间',
   primary key (id)
) comment '转换率历史统计表';

-- 添加陪玩师技能等级id
ALTER TABLE `t_user_info_auth` ADD COLUMN `tech_level_id` int(11) DEFAULT NULL
COMMENT '技能等级id' after `about`;

