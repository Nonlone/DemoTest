CREATE TABLE `test` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
	`value` VARCHAR(50) NOT NULL,
	`timestamp` DATETIME NOT NULL,
	`create_time` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;
