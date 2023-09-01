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

package com.yujunyang.iddd.common.infrastructure.persistence;

import java.util.List;
import java.util.stream.Collectors;

import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.domain.event.EventStore;
import com.yujunyang.iddd.common.domain.event.StoredEvent;
import com.yujunyang.iddd.common.infrastructure.persistence.mybatis.mapper.EventStoreMapper;
import com.yujunyang.iddd.common.infrastructure.persistence.mybatis.mapper.model.EventStoreModel;
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
        EventStoreModel eventStoreModel = new EventStoreModel();
        eventStoreModel.setEventBody(JacksonUtils.serialize(domainEvent));
        eventStoreModel.setEventType(domainEvent.storedEventType());
        eventStoreModel.setTimestamp(domainEvent.getTimestamp());
        eventStoreModel.setEventKey(domainEvent.eventKey());

        eventStoreMapper.insert(eventStoreModel);

        return convert(eventStoreModel);
    }

    @Override
    public <T extends DomainEvent> List<StoredEvent> storedEventsSince(
            Class<T> domainEventClass,
            long eventId,
            int limit) {
        List<EventStoreModel> eventStoreModels = eventStoreMapper.getListSince(domainEventClass.getName(), eventId, limit);
        return eventStoreModels.stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    StoredEvent convert(EventStoreModel eventStoreModel) {
        StoredEvent storedEvent = new StoredEvent(
                eventStoreModel.getId(),
                eventStoreModel.getEventBody(),
                eventStoreModel.getTimestamp(),
                eventStoreModel.getEventType(),
                eventStoreModel.getEventKey()
        );
        return storedEvent;
    }
}
