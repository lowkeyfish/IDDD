package com.yujunyang.iddd.common.domain.event;

import java.util.List;

public interface EventStore {

    StoredEvent add(DomainEvent domainEvent);

    <T extends DomainEvent> List<StoredEvent> storedEventsSince(
            Class<T> domainEventClass,
            long eventId,
            int limit);
}
