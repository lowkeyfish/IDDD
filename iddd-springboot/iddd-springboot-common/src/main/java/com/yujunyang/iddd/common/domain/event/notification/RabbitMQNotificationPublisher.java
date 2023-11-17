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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.domain.event.EventStore;
import com.yujunyang.iddd.common.domain.event.StoredEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RabbitMQNotificationPublisher implements NotificationPublisher {
    private EventStore eventStore;
    private PublishedNotificationTrackerStore publishedNotificationTrackerStore;
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQNotificationPublisher(
            EventStore eventStore,
            PublishedNotificationTrackerStore publishedNotificationTrackerStore,
            RabbitTemplate rabbitTemplate) {
        this.eventStore = eventStore;
        this.publishedNotificationTrackerStore = publishedNotificationTrackerStore;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public <T extends DomainEvent> boolean publishNotifications(
            Class<T> domainEventClass,
            int limit) {
        boolean unfinished = false;

        PublishedNotificationTracker publishedNotificationTracker =
                publishedNotificationTrackerStore.publishedNotificationTracker(domainEventClass);

        List<Notification> notifications = notifications(
                domainEventClass,
                publishedNotificationTracker.getMostRecentPublishedEventId(),
                limit);

        unfinished = notifications.size() == limit;

        if (notifications.isEmpty()) {
            return unfinished;
        }

        publishNotifications(notifications);

        publishedNotificationTrackerStore.trackMostRecentPublishedNotification(publishedNotificationTracker, notifications);

        return unfinished;
    }

    void publishNotifications(List<Notification> notifications) {
        notifications.forEach(notification -> {
            DomainEvent domainEvent = notification.getEvent();
            rabbitTemplate.convertAndSend(
                    domainEvent.notificationExchange(),
                    domainEvent.notificationRoutingKey(),
                    domainEvent,
                    m -> {
                        Map headers = m.getMessageProperties().getHeaders();
                        headers.put("Type", domainEvent.notificationType());
                        headers.put("Id", String.valueOf(notification.getNotificationId()));
                        return m;
                    });
        });
    }

    <T extends DomainEvent> List<Notification> notifications(Class<T> domainEventClass, long mostRecentPublishedEventId, int limit) {
        List<StoredEvent> notifications = eventStore.storedEventsSince(
                domainEventClass,
                mostRecentPublishedEventId,
                limit);

        return notifications.stream().map(n -> new Notification(
                    n.getEventId(),
                    n.toDomainEvent()
            )).collect(Collectors.toList());
    }
}
