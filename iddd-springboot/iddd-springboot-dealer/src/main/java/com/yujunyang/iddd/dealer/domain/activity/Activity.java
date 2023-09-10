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

import java.time.LocalDateTime;
import java.util.List;

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.common.utils.IdUtils;
import com.yujunyang.iddd.dealer.domain.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;

public class Activity {
    private DealerId dealerId;
    private ActivityId id;
    private String name;
    private String summary;
    private String image;
    private TimeRange visibleTimeRange;
    private TimeRange usableTimeRange;
    private int participantLimit;
    private int participantCount;
    private List<ActivityGift> gifts;
    private ActivityStatusType status;
    private boolean deleted;

    public Activity(
            DealerId dealerId,
            ActivityId id,
            String name,
            String summary,
            String image,
            TimeRange visibleTimeRange,
            TimeRange usableTimeRange,
            int participantLimit,
            int participantCount,
            List<ActivityGift> gifts,
            ActivityStatusType status,
            boolean deleted) {
        this.dealerId = dealerId;
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.image = image;
        this.visibleTimeRange = visibleTimeRange;
        this.usableTimeRange = usableTimeRange;
        this.participantLimit = participantLimit;
        this.participantCount = participantCount;
        this.gifts = gifts;
        this.status = status;
        this.deleted = deleted;
    }

    public Activity(
            DealerId dealerId,
            ActivityId id,
            String name,
            String summary,
            String image,
            TimeRange visibleTimeRange,
            TimeRange usableTimeRange,
            int participantLimit,
            List<ActivityGift> gifts) {
        this(
                dealerId,
                id,
                name,
                summary,
                image,
                visibleTimeRange,
                usableTimeRange,
                participantLimit,
                0,
                gifts,
                ActivityStatusType.PENDING,
                false
        );

        checkData(
                name,
                summary,
                image,
                visibleTimeRange,
                usableTimeRange,
                participantLimit,
                gifts
        );

        DomainEventPublisher.instance().publish(new ActivityCreated(
                dealerId.getId(),
                id.getId(),
                DateTimeUtilsEnhance.epochMilliSecond()
        ));
    }

    public void update(
            String name,
            String summary,
            String image,
            TimeRange visibleTimeRange,
            TimeRange usableTimeRange,
            int participantLimit,
            List<ActivityGift> gifts,
            ActivityNameUniquenessCheckService nameUniquenessCheckService) {
        if (ActivityStatusType.ONLINE.equals(status)) {
            throw new UnsupportedOperationException("活动已上线不支持修改");
        }
        if (ActivityStatusType.OFFLINE.equals(status)) {
            throw new UnsupportedOperationException("活动已下线不支持修改");
        }

        checkData(
                name,
                summary,
                image,
                visibleTimeRange,
                usableTimeRange,
                participantLimit,
                gifts);

        boolean nameUsed = nameUniquenessCheckService.isNameUsed(this, name);
        CheckUtils.isTrue(!nameUsed, "name 已被其他活动使用");

        this.name = name;
        this.summary = summary;
        this.image = image;
        this.visibleTimeRange = visibleTimeRange;
        this.usableTimeRange = usableTimeRange;
        this.participantLimit = participantLimit;

        DomainEventPublisher.instance().publish(new ActivityUpdated(
                dealerId.getId(),
                id.getId(),
                DateTimeUtilsEnhance.epochMilliSecond()
        ));
    }



    public ActivityRegistration register(
            Participant participant,
            ActivityRegistrationLimitService registrationLimitService) {
        CheckUtils.notNull(participant, "participant 必须不为 null");

        if (!status.equals(ActivityStatusType.ONLINE)) {
            throw new UnsupportedOperationException("活动非上线状态");
        }

        if (participantLimit > participantCount) {
            throw new UnsupportedOperationException("活动参与人数已达上限");
        }

        boolean restricted = registrationLimitService.isRestricted(this, participant);
        if (restricted) {
            throw new UnsupportedOperationException("用户参与活动次数已达到限制");
        }

        participantCount++;

        return new ActivityRegistration(
                new ActivityRegistrationId(IdUtils.newId()),
                dealerId,
                id,
                participant,
                LocalDateTime.now(),
                usableTimeRange
        );
    }

    public DealerId getDealerId() {
        return dealerId;
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

    public ActivityStatusType getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    private void checkData(
            String name,
            String summary,
            String image,
            TimeRange visibleTimeRange,
            TimeRange usableTimeRange,
            int participantLimit, List<ActivityGift> gifts) {
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(summary, "summary 必须不为空");
        CheckUtils.notBlank(image, "image 必须不为空");
        CheckUtils.notNull(visibleTimeRange, "visibleTimeRange 必须不为 null");
        CheckUtils.notNull(usableTimeRange, "usableTimeRange 必须不为 null");
        CheckUtils.moreThan(participantLimit, 0, "participantLimit 必须大于 0");
    }
}
