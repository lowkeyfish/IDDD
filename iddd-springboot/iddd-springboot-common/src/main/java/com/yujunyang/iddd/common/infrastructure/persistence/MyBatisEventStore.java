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
import java.util.stream.Collectors;

import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.domain.event.EventStore;
import com.yujunyang.iddd.common.domain.event.StoredEvent;
import com.yujunyang.iddd.common.infrastructure.persistence.mybatis.mapper.EventStoreMapper;
import com.yujunyang.iddd.common.infrastructure.persistence.mybatis.mapper.model.EventStoreDatabaseModel;
import com.yujunyang.iddd.common.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisEventStore implements EventStore {
    private EventStoreMapper eventStoreMapper;

    @Autowired
    public MyBatisEventStore(
            EventStoreMapper eventStoreMapper) {
        this.eventStoreMapper = eventStoreMapper;
    }

    @Override
    public StoredEvent add(DomainEvent domainEvent) {
        EventStoreDatabaseModel eventStoreDatabaseModel = new EventStoreDatabaseModel();
        eventStoreDatabaseModel.setEventBody(JacksonUtils.serialize(domainEvent));
        eventStoreDatabaseModel.setEventType(domainEvent.storedEventType());
        eventStoreDatabaseModel.setTimestamp(domainEvent.getTimestamp());
        eventStoreDatabaseModel.setEventKey(domainEvent.eventKey());

        eventStoreMapper.insert(eventStoreDatabaseModel);

        return convert(eventStoreDatabaseModel);
    }

    @Override
    public <T extends DomainEvent> List<StoredEvent> storedEventsSince(
            Class<T> domainEventClass,
            long eventId,
            int limit) {
        List<EventStoreDatabaseModel> eventStoreDatabaseModels = eventStoreMapper.getListSince(domainEventClass.getName(), eventId, limit);
        return eventStoreDatabaseModels.stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    StoredEvent convert(EventStoreDatabaseModel eventStoreDatabaseModel) {
        StoredEvent storedEvent = new StoredEvent(
                eventStoreDatabaseModel.getId(),
                eventStoreDatabaseModel.getEventBody(),
                eventStoreDatabaseModel.getTimestamp(),
                eventStoreDatabaseModel.getEventType(),
                eventStoreDatabaseModel.getEventKey()
        );
        return storedEvent;
    }
}
