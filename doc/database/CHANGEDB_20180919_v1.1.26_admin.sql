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


