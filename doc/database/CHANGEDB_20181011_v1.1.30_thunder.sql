-- 注释修改
ALTER TABLE `t_banner` MODIFY COLUMN `platform_type` tinyint(1) DEFAULT '1' COMMENT 'banner所属平台(1:小程序;2:app;3:迅雷约玩-首页;4:迅雷约玩-列表页)';