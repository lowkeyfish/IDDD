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
 *
 */

package com.yujunyang.iddd.common.domain.event.notification;

import java.text.MessageFormat;

import com.yujunyang.iddd.common.domain.event.DomainEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger LOGGER = LogManager.getLogger();

    private NotificationPublisher notificationPublisher;
    private RedissonClient redissonClient;

    @Autowired
    public NotificationService(
            NotificationPublisher notificationPublisher,
            RedissonClient redissonClient) {
        this.notificationPublisher = notificationPublisher;
        this.redissonClient = redissonClient;
    }

    public <T extends DomainEvent> void publishNotifications(Class<T> domainEventClass, int limit) {
        String lockKey = MessageFormat.format("NotificationApplicationService_{0}", domainEventClass.getName());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock()) {
                while (notificationPublisher.publishNotifications(domainEventClass, limit)) {
                }

            } else {
                LOGGER.warn("事件通知, DomainEvent({}) 获取锁失败.", domainEventClass.getName());
            }
        } catch (Exception e) {
            LOGGER.error("事件通知, DomainEvent({}) 出错", domainEventClass.getName(), e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
