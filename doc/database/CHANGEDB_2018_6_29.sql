//cdk批次增加备注字段
ALTER TABLE t_cdk_group ADD COLUMN remark VARCHAR(255) DEFAULT NULL COMMENT "备注" AFTER channel_name;
//user表增加如下字段
ALTER TABLE t_user ADD COLUMN regist_ip VARCHAR(128) DEFAULT NULL COMMENT "注册ip" AFTER source_id;
ALTER TABLE t_user ADD COLUMN login_ip VARCHAR(128) DEFAULT NULL COMMENT "最后登录ip" AFTER regist_ip;
ALTER TABLE t_user ADD COLUMN login_time datetime COMMENT "最后登录时间" AFTER login_ip;