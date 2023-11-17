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

package com.yujunyang.iddd.common.domain.event;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainEventRabbitMQSyncPublisher {
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public DomainEventRabbitMQSyncPublisher(
            RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(DomainEvent domainEvent) {
        rabbitTemplate.convertAndSend(
                domainEvent.notificationExchange(),
                domainEvent.notificationRoutingKey(),
                domainEvent,
                m -> {
                    Map headers = m.getMessageProperties().getHeaders();
                    headers.put("Type", domainEvent.notificationType());
                    headers.put("Id", UUID.randomUUID().toString().replace("-", ""));
                    return m;
                });
    }
}
