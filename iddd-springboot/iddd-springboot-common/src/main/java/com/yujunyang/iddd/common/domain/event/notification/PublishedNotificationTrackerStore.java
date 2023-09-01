package com.yujunyang.iddd.common.domain.event.notification;

import java.util.List;

import com.yujunyang.iddd.common.domain.event.DomainEvent;

public interface PublishedNotificationTrackerStore {

    <T extends DomainEvent> PublishedNotificationTracker publishedNotificationTracker(Class<T> domainEventClass);

    void trackMostRecentPublishedNotification(
            PublishedNotificationTracker publishedNotificationTracker,
            List<Notification> notifications);
}
