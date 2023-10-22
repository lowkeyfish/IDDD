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

package com.yujunyang.iddd.dealer.domain.activity;

import java.util.List;

import com.yujunyang.iddd.dealer.common.TimeRange;

public class ActivitySnapshot {
    private ActivityId id;
    private String name;
    private String summary;
    private String image;
    private TimeRange visibleTimeRange;
    private TimeRange usableTimeRange;
    private int participantLimit;
    private List<ActivityGiftSnapshot> gifts;
    private ActivityStatusType registrationStatus;
    private ActivityStatusType participationStatus;
    private boolean deleted;

    ActivitySnapshot(
            ActivityId id,
            String name,
            String summary,
            String image,
            TimeRange visibleTimeRange,
            TimeRange usableTimeRange,
            int participantLimit,
            List<ActivityGiftSnapshot> gifts,
            ActivityStatusType registrationStatus,
            ActivityStatusType participationStatus,
            boolean deleted) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.image = image;
        this.visibleTimeRange = visibleTimeRange;
        this.usableTimeRange = usableTimeRange;
        this.participantLimit = participantLimit;
        this.gifts = gifts;
        this.registrationStatus = registrationStatus;
        this.participationStatus = participationStatus;
        this.deleted = deleted;
    }

    public ActivityId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public String getImage() {
        return image;
    }

    public TimeRange getVisibleTimeRange() {
        return visibleTimeRange;
    }

    public TimeRange getUsableTimeRange() {
        return usableTimeRange;
    }

    public int getParticipantLimit() {
        return participantLimit;
    }

    public List<ActivityGiftSnapshot> getGifts() {
        return gifts;
    }

    public ActivityStatusType getRegistrationStatus() {
        return registrationStatus;
    }

    public ActivityStatusType getParticipationStatus() {
        return participationStatus;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
