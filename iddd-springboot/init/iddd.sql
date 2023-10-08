/*
 * Copyright 2023 Yu Junyang
 * https://github.com/lowkeyfish
 *
 * This file is part of IDDD.
 *
 * IDDD is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IDDD is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IDDD.
 * If not, see <http://www.gnu.org/licenses/>.
 */

CREATE DATABASE `iddd` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `iddd`;

SET GLOBAL time_zone = '+08:00';
SET time_zone = '+08:00';



DROP TABLE IF EXISTS
    `brand`,
    `manufacturer`,
    `model`,
    `variant`,
    `event_store`,
    `published_notification_tracker`,
    `dealer`,
    `dealer_model`,
    `dealer_variant`,
    `activity`,
    `activity_registration`,
    `sales_advisor`;

CREATE TABLE `brand` (
                         `id` bigint NOT NULL,
                         `name` varchar(200) NOT NULL DEFAULT '',
                         `first_letter` char(1) NOT NULL DEFAULT '',
                         `logo` VARCHAR(500) NOT NULL DEFAULT '',
                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `deleted` tinyint NOT NULL DEFAULT '0',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `manufacturer` (
                                `id` bigint NOT NULL,
                                `name` varchar(200) NOT NULL DEFAULT '',
                                `brand_id` bigint NOT NULL DEFAULT 0,
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `deleted` tinyint NOT NULL DEFAULT '0',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `model` (
                         `id` bigint NOT NULL,
                         `name` varchar(200) NOT NULL DEFAULT '',
                         `image` VARCHAR(500) NOT NULL DEFAULT '',
                         `brand_id` bigint NOT NULL DEFAULT 0,
                         `manufacturer_id` bigint NOT NULL DEFAULT 0,
                         `price_min` int NOT NULL DEFAULT 0,
                         `price_max` int NOT NULL DEFAULT 0,
                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `deleted` tinyint NOT NULL DEFAULT '0',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uniq` (`name`, `brand_id`, `manufacturer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `variant` (
                           `id` bigint NOT NULL,
                           `name` varchar(200) NOT NULL DEFAULT '',
                           `model_id` bigint NOT NULL DEFAULT 0,
                           `brand_id` bigint NOT NULL DEFAULT 0,
                           `manufacturer_id` bigint NOT NULL DEFAULT 0,
                           `price` int NOT NULL DEFAULT 0,
                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           `deleted` tinyint NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uniq` (`name`, `model_id`, `brand_id`, `manufacturer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `event_store` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                               `event_type` varchar(500) NOT NULL DEFAULT '' COMMENT '事件类型',
                               `event_body` json NOT NULL,
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
                               `timestamp` bigint NOT NULL DEFAULT '0' COMMENT '事件发生时间戳',
                               `event_key` varchar(500) NOT NULL DEFAULT '' COMMENT '键',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='事件表';

CREATE TABLE `published_notification_tracker` (
                                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                                  `most_recent_published_event_id` bigint NOT NULL DEFAULT '0' COMMENT '最新发布的事件id',
                                                  `event_type` varchar(500) NOT NULL DEFAULT '' COMMENT '事件类型',
                                                  `concurrency_version` bigint NOT NULL DEFAULT '0' COMMENT '控制并发的版本',
                                                  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
                                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
                                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                  PRIMARY KEY (`id`),
                                                  UNIQUE KEY `uniq_event_type` (`event_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用于记录已发送过的事件通知';

CREATE TABLE `dealer` (
                          `id` bigint NOT NULL,
                          `name` varchar(500) NOT NULL DEFAULT '',
                          `city_id` int not null default 0,
                          `specific_address` varchar(1000) NOT NULL DEFAULT '',
                          `telephone` varchar(20) NOT NULL DEFAULT '',
                          `brand_id` bigint NOT NULL DEFAULT 0,
                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `deleted` tinyint NOT NULL DEFAULT 0,
                          `status` tinyint not null default 0,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `dealer_model` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `dealer_id` bigint NOT NULL DEFAULT 0,
                                `brand_id` bigint NOT NULL DEFAULT 0,
                                `manufacturer_id` bigint NOT NULL DEFAULT 0,
                                `model_id` bigint NOT NULL DEFAULT 0,
                                `sale_status` tinyint not null default 0,
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `deleted` tinyint NOT NULL DEFAULT '0',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `dealer_variant` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `dealer_id` bigint NOT NULL DEFAULT 0,
                                  `brand_id` bigint NOT NULL DEFAULT 0,
                                  `manufacturer_id` bigint NOT NULL DEFAULT 0,
                                  `model_id` bigint NOT NULL DEFAULT 0,
                                  `variant_id` bigint NOT NULL DEFAULT 0,
                                  `price` int not null default 0,
                                  `sale_status` tinyint not null default 0,
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  `deleted` tinyint NOT NULL DEFAULT '0',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `activity` (
                            `id` bigint NOT NULL,
                            `dealer_id` bigint NOT NULL DEFAULT 0,
                            `name` varchar(200) not null default '',
                            `summary` varchar(500) not null default '',
                            `image` varchar(500) not null default '',
                            `online_time` datetime not null,
                            `offline_time` datetime not null,
                            `usable_time_begin` datetime not null,
                            `usable_time_end` datetime not null,
                            `participant_limit` int not null default 0,
                            `status` tinyint not null default 0,
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `deleted` tinyint NOT NULL DEFAULT '0',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `activity_registration` (
                                         `id` bigint NOT NULL,
                                         `activity_id` bigint NOT NULL DEFAULT 0,
                                         `dealer_id` bigint NOT NULL DEFAULT 0,
                                         `user_name` varchar(100) not null default '',
                                         `user_mobile_number` varchar(100) not null default '',
                                         `usable_time_begin` datetime not null,
                                         `usable_time_end` datetime not null,
                                         `verification_code` varchar(50) not null,
                                         `registration_time` datetime not null,
                                         `use_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `status` tinyint not null default 0,
                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         `deleted` tinyint NOT NULL DEFAULT '0',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sales_advisor` (
                                 `id` bigint NOT NULL,
                                 `dealer_id` bigint NOT NULL DEFAULT 0,
                                 `name` varchar(100) not null default '',
                                 `mobile_number` varchar(100) not null default '',
                                 `avatar` varchar(500) not null default '',
                                 `status` tinyint not null default 0,
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `deleted` tinyint NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET @brandId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `brand`(`id`,`name`,`first_letter`,`logo`) VALUES
    (@brandId, '奥迪','A','/images/brand/audi.png');
SELECT SLEEP(0.01);
SET @manufacturerId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `manufacturer`(`id`,`name`,`brand_id`) VALUES
    (@manufacturerId,'上汽奥迪',@brandId);
SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪Q5 e-tron',@brandId,@manufacturerId,'/images/model/q5e-tron.png',298500,435500);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 40 e-tron 闪耀型 锦衣套装',298500,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2022款 50 e-tron quattro edition one 艺创典藏版',435500,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @manufacturerId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `manufacturer`(`id`,`name`,`brand_id`) VALUES
    (@manufacturerId,'一汽奥迪',@brandId);
SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪A6L',@brandId,@manufacturerId,'/images/model/a6l.png',472900,656800);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 改款 40 TFSI 豪华致雅型',427900,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 55 TFSI quattro 尊享动感型',558900,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 55 TFSI quattro 旗舰动感型',656800,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪A4L',@brandId,@manufacturerId,'/images/model/a4l.png',321800,399800);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 40 TFSI 时尚致雅型',321800,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 45 TFSI quattro 臻选动感型',399800,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪Q5L',@brandId,@manufacturerId,'/images/model/q5l.png',396800,488800);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2022款 40T 时尚动感型',396800,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 45T 臻选动感型',488800,@brandId,@manufacturerId,@modelId);

SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪A3',@brandId,@manufacturerId,'/images/model/a3.png',203100,251300);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 改款 Sportback 35 TFSI 进取运动型',203100,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 A3L Limousine 35 TFSI 豪华运动型',251200,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪Q3',@brandId,@manufacturerId,'/images/model/q3.png',278800,335900);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 35 TFSI 进取动感型（1.4T）',278800,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'22024款 45 TFSI quattro 时尚动感型上市版',335900,@brandId,@manufacturerId,@modelId);

SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪Q2L',@brandId,@manufacturerId,'/images/model/q2l.png',222800,268800);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 35TFSI 进取致雅型',222800,@brandId,@manufacturerId,@modelId);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'2023款 35 TFSI 豪华动感型',268800,@brandId,@manufacturerId,@modelId);

SELECT SLEEP(0.01);
SET @modelId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `model`(`id`,`name`,`brand_id`,`manufacturer_id`,`image`,`price_min`,`price_max`) VALUES
    (@modelId,'奥迪Q2L e-tron',@brandId,@manufacturerId,'/images/model/q2le-tron.png',243800,243800);
SELECT SLEEP(0.01);
SET @variantId = (ROUND(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) - 1696118400000) << 22 | 1 << 10 | 0;
INSERT INTO `variant`(`id`,`name`,`price`,`brand_id`,`manufacturer_id`,`model_id`) VALUES
    (@variantId,'22022款 Q2L e-tron 纯电智享型',243800,@brandId,@manufacturerId,@modelId);


