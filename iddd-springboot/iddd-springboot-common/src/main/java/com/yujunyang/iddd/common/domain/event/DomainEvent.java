package com.yujunyang.iddd.common.domain.event;

public interface DomainEvent {

    long getTimestamp();

    String eventKey();

    String storedEventType();

    String notificationType();

    String notificationExchange();

    String notificationRoutingKey();
}
