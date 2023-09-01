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

CREATE TABLE `event_store` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `event_type` varchar(500) NOT NULL DEFAULT '' COMMENT '事件类型',
    `event_body` json NOT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
    `timestamp` bigint NOT NULL DEFAULT '0' COMMENT '事件发生时间戳',
    `event_key` varchar(500) NOT NULL DEFAULT '' COMMENT '键',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='事件表';

CREATE TABLE `published_notification_tracker` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `most_recent_published_event_id` bigint NOT NULL DEFAULT '0' COMMENT '最新发布的事件id',
    `event_type` varchar(500) NOT NULL DEFAULT '' COMMENT '事件类型',
    `concurrency_version` bigint NOT NULL DEFAULT '0' COMMENT '控制并发的版本',
    `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_event_type` (`event_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用于记录已发送过的事件通知';