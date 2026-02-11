ALTER TABLE `daily_stats` ADD COLUMN `total_taps` INT DEFAULT 0 COMMENT '每日键盘敲击总数';
ALTER TABLE `daily_stats` ADD COLUMN `total_harvests` INT DEFAULT 0 COMMENT '每日农场收获总数';
ALTER TABLE `daily_stats` ADD COLUMN `most_active_key` VARCHAR(20) DEFAULT '' COMMENT '每日最热键';
