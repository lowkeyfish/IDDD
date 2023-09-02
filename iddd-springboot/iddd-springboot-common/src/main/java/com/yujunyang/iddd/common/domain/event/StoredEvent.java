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
