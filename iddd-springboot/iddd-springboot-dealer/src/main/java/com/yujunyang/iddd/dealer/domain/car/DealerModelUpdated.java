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

package com.yujunyang.iddd.dealer.domain.car;

import java.text.MessageFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yujunyang.iddd.common.domain.event.DomainEvent;

public class DealerModelUpdated implements DomainEvent {
    private long timestamp;
    private long dealerId;
    private long modelId;

    @JsonCreator
    public DealerModelUpdated(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("dealerId") long dealerId,
            @JsonProperty("modelId") long modelId) {
        this.timestamp = timestamp;
        this.dealerId = dealerId;
        this.modelId = modelId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public long getDealerId() {
        return dealerId;
    }

    public long getModelId() {
        return modelId;
    }

    @Override
    public String eventKey() {
        return MessageFormat.format("DealerId({0})_ModelId({1})", dealerId, modelId);
    }

    @Override
    public String notificationRoutingKey() {
        return "DealerModel." + notificationType();
    }
}
