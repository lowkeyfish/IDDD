package com.yujunyang.iddd.common.domain.event.notification;

import com.yujunyang.iddd.common.domain.event.DomainEvent;

public interface NotificationPublisher {
    <T extends DomainEvent> boolean publishNotifications(Class<T> domainEventClass, int limit);

}
