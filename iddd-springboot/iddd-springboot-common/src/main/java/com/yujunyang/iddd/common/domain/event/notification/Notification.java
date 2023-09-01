package com.yujunyang.iddd.common.domain.event.notification;

import com.yujunyang.iddd.common.domain.event.DomainEvent;

public class Notification {
    private DomainEvent event;
    private long notificationId;

    public Notification(
            long notificationId,
            DomainEvent event) {
        this.notificationId = notificationId;
        this.event = event;
    }

    public DomainEvent getEvent() {
        return event;
    }

    public long getNotificationId() {
        return notificationId;
    }

}
