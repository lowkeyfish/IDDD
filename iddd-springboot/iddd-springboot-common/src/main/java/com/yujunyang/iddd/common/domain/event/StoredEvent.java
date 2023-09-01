package com.yujunyang.iddd.common.domain.event;

import com.yujunyang.iddd.common.utils.JacksonUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class StoredEvent {
    private long eventId;
    private String eventBody;
    private long timestamp;
    private String typeName;
    private String eventKey;

    public StoredEvent(
            String eventBody,
            long timestamp,
            String typeName,
            String eventKey) {
        this.eventBody = eventBody;
        this.timestamp = timestamp;
        this.typeName = typeName;
        this.eventKey = eventKey;
    }

    public StoredEvent(
            long eventId,
            String eventBody,
            long timestamp,
            String typeName,
            String eventKey) {
        this.eventId = eventId;
        this.eventBody = eventBody;
        this.timestamp = timestamp;
        this.typeName = typeName;
        this.eventKey = eventKey;
    }

    public <T extends DomainEvent> T toDomainEvent() {
        Class<T> domainEventClass = null;

        try {
            domainEventClass = (Class<T>) Class.forName(this.typeName());
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Class load error, because: "
                            + e.getMessage());
        }

        T domainEvent = JacksonUtils.deSerialize(getEventBody(), domainEventClass);

        return domainEvent;
    }

    public String typeName() {
        return this.typeName;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            StoredEvent typedObject = (StoredEvent) anObject;
            equalObjects = this.eventId == typedObject.eventId;
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(77, 772371).append(eventId).toHashCode();
    }

    public long getEventId() {
        return eventId;
    }

    public String getEventBody() {
        return eventBody;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getEventKey() {
        return eventKey;
    }
}
