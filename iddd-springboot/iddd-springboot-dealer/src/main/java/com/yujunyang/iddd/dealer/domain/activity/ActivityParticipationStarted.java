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

package com.yujunyang.iddd.dealer.domain.activity;

import java.text.MessageFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.utils.CheckUtils;

public class ActivityParticipationStarted implements DomainEvent {
    private long dealerId;
    private long activityId;
    private long timestamp;

    @JsonCreator
    public ActivityParticipationStarted(
            @JsonProperty("dealerId") long dealerId,
            @JsonProperty("activityId") long activityId,
            @JsonProperty("timestamp") long timestamp) {
        CheckUtils.moreThan(dealerId, 0, "dealer 必须大于 0");
        CheckUtils.moreThan(activityId, 0, "activityId 必须大于 0");
        CheckUtils.moreThan(timestamp, 0, "timestamp 必须大于 0");

        this.dealerId = dealerId;
        this.activityId = activityId;
        this.timestamp = timestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public long getDealerId() {
        return dealerId;
    }

    public long getActivityId() {
        return activityId;
    }

    @Override
    public String eventKey() {
        return MessageFormat.format("DealerId({0})_ActivityId({1})", dealerId, activityId);
    }

    @Override
    public String notificationRoutingKey() {
        return "Activity." + notificationType();
    }
}
