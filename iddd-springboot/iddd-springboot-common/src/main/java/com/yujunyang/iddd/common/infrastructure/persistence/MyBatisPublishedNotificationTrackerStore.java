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

package com.yujunyang.iddd.common.infrastructure.persistence;

import java.util.List;

import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.domain.event.notification.Notification;
import com.yujunyang.iddd.common.domain.event.notification.PublishedNotificationTracker;
import com.yujunyang.iddd.common.domain.event.notification.PublishedNotificationTrackerStore;
import com.yujunyang.iddd.common.infrastructure.persistence.mybatis.mapper.PublishedNotificationTrackerMapper;
import com.yujunyang.iddd.common.infrastructure.persistence.mybatis.mapper.model.PublishedNotificationTrackerDatabaseModel;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyBatisPublishedNotificationTrackerStore implements PublishedNotificationTrackerStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisPublishedNotificationTrackerStore.class);

    private PublishedNotificationTrackerMapper publishedNotificationTrackerMapper;

    @Autowired
    public MyBatisPublishedNotificationTrackerStore(
            PublishedNotificationTrackerMapper publishedNotificationTrackerMapper) {
        this.publishedNotificationTrackerMapper = publishedNotificationTrackerMapper;
    }

    @Override
    public <T extends DomainEvent> PublishedNotificationTracker publishedNotificationTracker(Class<T> domainEventClass) {
        String eventType = domainEventClass.getName();

        PublishedNotificationTrackerDatabaseModel publishedNotificationTrackerDatabaseModel = publishedNotificationTrackerMapper.getByEventType(eventType);
        if (publishedNotificationTrackerDatabaseModel == null) {
            return new PublishedNotificationTracker(eventType, 0);
        }

        PublishedNotificationTracker ret = new PublishedNotificationTracker(
                publishedNotificationTrackerDatabaseModel.getId(),
                publishedNotificationTrackerDatabaseModel.getMostRecentPublishedEventId(),
                eventType,
                publishedNotificationTrackerDatabaseModel.getConcurrencyVersion()
        );

        return ret;
    }

    @Override
    public void trackMostRecentPublishedNotification(
            PublishedNotificationTracker publishedNotificationTracker,
            List<Notification> notifications) {
        int lastIndex = notifications.size() - 1;

        if (lastIndex < 0) {
            return;
        }

        long mostRecentId = notifications.get(lastIndex).getNotificationId();

        PublishedNotificationTrackerDatabaseModel publishedNotificationTrackerDatabaseModel = new PublishedNotificationTrackerDatabaseModel();
        publishedNotificationTrackerDatabaseModel.setMostRecentPublishedEventId(mostRecentId);
        publishedNotificationTrackerDatabaseModel.setEventType(publishedNotificationTracker.getEventType());
        publishedNotificationTrackerDatabaseModel.setConcurrencyVersion(DateTimeUtilsEnhance.epochMilliSecond());
        publishedNotificationTrackerDatabaseModel.setId(publishedNotificationTracker.getPublishedNotificationTrackerId());

        if (publishedNotificationTrackerDatabaseModel.getId() == 0) {
            if (publishedNotificationTrackerMapper.insertOrIgnore(publishedNotificationTrackerDatabaseModel) == 0) {
                LOGGER.error("事件发布跟踪数据库插入新记录有竞争情况");
            }
        } else {
            if (publishedNotificationTrackerMapper.update(
                    publishedNotificationTrackerDatabaseModel.getId(),
                    publishedNotificationTrackerDatabaseModel.getMostRecentPublishedEventId(),
                    publishedNotificationTrackerDatabaseModel.getConcurrencyVersion(),
                    publishedNotificationTracker.getConcurrencyVersion()) == 0) {
                LOGGER.error("事件发布跟踪数据库更新记录有竞争情况");
            }
        }

    }
}
