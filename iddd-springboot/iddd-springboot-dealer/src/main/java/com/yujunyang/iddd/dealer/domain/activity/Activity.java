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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.common.utils.IdUtils;
import com.yujunyang.iddd.dealer.domain.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.gift.GiftId;

public class Activity {
    private DealerId dealerId;
    private ActivityId id;
    private String name;
    private String summary;
    private String image;
    private TimeRange registrationTimeRange;
    private TimeRange participationTimeRange;
    private int participantLimit;
    private List<ActivityGift> gifts;
    private ActivityStatusType registrationStatus;
    private ActivityStatusType participationStatus;
    private boolean deleted;
    private boolean updated;

    public Activity(
            DealerId dealerId,
            ActivityId id,
            String name,
            String summary,
            String image,
            TimeRange registrationTimeRange,
            TimeRange participationTimeRange,
            int participantLimit,
            List<ActivityGift> gifts,
            ActivityStatusType registrationStatus,
            ActivityStatusType participationStatus,
            boolean deleted) {
        this.dealerId = dealerId;
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.image = image;
        this.registrationTimeRange = registrationTimeRange;
        this.participationTimeRange = participationTimeRange;
        this.participantLimit = participantLimit;
        this.gifts = gifts;
        this.registrationStatus = registrationStatus;
        this.participationStatus = participationStatus;
        this.deleted = deleted;
    }

    public Activity(
            DealerId dealerId,
            ActivityId id,
            String name,
            String summary,
            String image,
            TimeRange registrationTimeRange,
            TimeRange participationTimeRange,
            int participantLimit,
            Map<GiftId, Integer> gifts) {
        this(
                dealerId,
                id,
                name,
                summary,
                image,
                registrationTimeRange,
                participationTimeRange,
                participantLimit,
                gifts.entrySet().stream().map(n -> new ActivityGift(
                        n.getKey(),
                        n.getValue()
                )).collect(Collectors.toList()),
                ActivityStatusType.PENDING,
                ActivityStatusType.PENDING,
                false
        );

        checkData(
                name,
                summary,
                image,
                registrationTimeRange,
                participationTimeRange,
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
            Map<GiftId, Integer> gifts,
            ActivityNameUniquenessCheckService nameUniquenessCheckService) {
        if (ActivityStatusType.STARTED.equals(status)) {
            throw new UnsupportedOperationException("活动已开始不支持修改");
        }
        if (ActivityStatusType.ENDED.equals(status)) {
            throw new UnsupportedOperationException("活动已结束不支持修改");
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
        this.registrationTimeRange = visibleTimeRange;
        this.participationTimeRange = usableTimeRange;
        this.participantLimit = participantLimit;

        DomainEventPublisher.instance().publish(new ActivityUpdated(
                dealerId.getId(),
                id.getId(),
                DateTimeUtilsEnhance.epochMilliSecond()
        ));
    }

    public void rename(
            String name,
            ActivityNameUniquenessCheckService nameUniquenessCheckService) {
        if (ActivityStatusType.PENDING.equals(status)) {
            throw new UnsupportedOperationException("活动只支持在待开始状态修改名称");
        }

        CheckUtils.isTrue(
                !nameUniquenessCheckService.isNameUsed(this, name),
                "名称({0})已被使用",
                name
        );

        this.name = name;

        updated();
    }

    public void delay() {
        if (ActivityStatusType.ENDED.equals(status)) {
            throw new UnsupportedOperationException("活动已开始不支持延期");
        }

        registrationTimeRange.delayEnd(30, ChronoUnit.DAYS);
        participationTimeRange.delayEnd(30, ChronoUnit.DAYS);

        updated();
    }

    public void start() {
        if (ActivityStatusType.STARTED.equals(status)) {
            return;
        }

        if (ActivityStatusType.ENDED.equals(status)) {
            throw new UnsupportedOperationException("活动已结束");
        }

        if (!ActivityStatusType.PENDING.equals(status)) {
            throw new UnsupportedOperationException("活动非待开始状态");
        }

        if (!registrationTimeRange.inProgress()) {
            if (registrationTimeRange.notStarted()) {
                throw new UnsupportedOperationException("活动未到开始时间");
            }
            if (registrationTimeRange.ended()) {
                throw new UnsupportedOperationException("活动已过结束时间");
            }
        }

        status = ActivityStatusType.STARTED;

        DomainEventPublisher.instance().publish(new ActivityStarted(
                dealerId.getId(),
                id.getId(),
                DateTimeUtilsEnhance.epochMilliSecond()
        ));
    }

    public void stop() {
        if (ActivityStatusType.ENDED.equals(status)) {
            return;
        }

        if (!ActivityStatusType.STARTED.equals(status)) {
            throw new UnsupportedOperationException("活动非开始状态");
        }

        if (!registrationTimeRange.ended()) {
            throw new UnsupportedOperationException("活动未到结束时间");
        }

        status = ActivityStatusType.ENDED;

        DomainEventPublisher.instance().publish(new ActivityEnded(
                dealerId.getId(),
                id.getId(),
                DateTimeUtilsEnhance.epochMilliSecond()
        ));
    }

    public ActivityRegistration register(
            Participant participant,
            ActivityRegistrationLimitService registrationLimitService) {
        CheckUtils.notNull(participant, "participant 必须不为 null");

        if (!status.equals(ActivityStatusType.STARTED)) {
            throw new UnsupportedOperationException("活动非开始状态");
        }

        boolean restricted = registrationLimitService.isRestricted(this, participant);
        if (restricted) {
            throw new UnsupportedOperationException("用户参与活动次数已达到限制");
        }

        return new ActivityRegistration(
                new ActivityRegistrationId(IdUtils.newId()),
                dealerId,
                id,
                participant,
                LocalDateTime.now(),
                participationTimeRange
        );
    }

    public ActivityId id() {
        return id;
    }



    public ActivitySnapshot snapshot() {
        return new ActivitySnapshot(
                id,
                name,
                summary,
                image,
                registrationTimeRange,
                participationTimeRange,
                participantLimit,
                gifts.stream().map(n -> new ActivityGiftSnapshot(
                        n.id(),
                        n.getCount()
                )).collect(Collectors.toList()),
                status,
                deleted
        );
    }

    private void updated() {
        if (!updated) {
            updated = true;
            DomainEventPublisher.instance().publish(new ActivityUpdated(
                    dealerId.getId(),
                    id.getId(),
                    DateTimeUtilsEnhance.epochMilliSecond()
            ));
        }
    }

    private void checkData(
            String name,
            String summary,
            String image,
            TimeRange visibleTimeRange,
            TimeRange usableTimeRange,
            int participantLimit,
            Map<GiftId, Integer> gifts) {
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(summary, "summary 必须不为空");
        CheckUtils.notBlank(image, "image 必须不为空");
        CheckUtils.notNull(visibleTimeRange, "visibleTimeRange 必须不为 null");
        CheckUtils.notNull(usableTimeRange, "usableTimeRange 必须不为 null");
        CheckUtils.moreThan(participantLimit, 0, "participantLimit 必须大于 0");
    }
}
