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
 */

package com.yujunyang.iddd.common.infrastructure.persistence.mybatis.mapper.model;

public class PublishedNotificationTrackerModel {
    private long id;
    private long mostRecentPublishedEventId;
    private String eventType;
    private long concurrencyVersion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMostRecentPublishedEventId() {
        return mostRecentPublishedEventId;
    }

    public void setMostRecentPublishedEventId(long mostRecentPublishedEventId) {
        this.mostRecentPublishedEventId = mostRecentPublishedEventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getConcurrencyVersion() {
        return concurrencyVersion;
    }

    public void setConcurrencyVersion(long concurrencyVersion) {
        this.concurrencyVersion = concurrencyVersion;
    }
}
