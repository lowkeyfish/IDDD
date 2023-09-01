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
