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
import java.util.Map;
import java.util.stream.Collectors;

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.common.utils.IdUtils;
import com.yujunyang.iddd.dealer.common.TimeRange;
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
            ActivityId id,
            DealerId dealerId,
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
            ActivityId id,
            DealerId dealerId,
            String name,
            String summary,
            String image,
            TimeRange registrationTimeRange,
            TimeRange participationTimeRange,
            int participantLimit,
            Map<GiftId, Integer> gifts) {
        this(
                id,
                dealerId,
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
            TimeRange registrationTimeRange,
            TimeRange participationTimeRange,
            int participantLimit,
            Map<GiftId, Integer> gifts,
            ActivityNameUniquenessCheckService activityNameUniquenessCheckService) {
        CheckUtils.isTrue(
                ActivityStatusType.PENDING.equals(registrationStatus),
                new IllegalStateException("活动报名开始后不支持修改")
        );

        checkData(
                name,
                summary,
                image,
                registrationTimeRange,
                participationTimeRange,
                participantLimit,
                gifts);

        CheckUtils.isTrue(
                !activityNameUniquenessCheckService.isNameUsed(this, name),
                "名称({0})已被使用",
                name
        );

        this.name = name;
        this.summary = summary;
        this.image = image;
        this.registrationTimeRange = registrationTimeRange;
        this.participationTimeRange = participationTimeRange;
        this.participantLimit = participantLimit;

        DomainEventPublisher.instance().publish(new ActivityUpdated(
                dealerId.getId(),
                id.getId(),
                DateTimeUtilsEnhance.epochMilliSecond()
        ));
    }

    public void rename(
            String name,
            ActivityNameUniquenessCheckService activityNameUniquenessCheckService) {
        CheckUtils.isTrue(
                ActivityStatusType.PENDING.equals(registrationStatus),
                new IllegalStateException("活动报名开始后不支持修改")
        );

        CheckUtils.isTrue(
                !activityNameUniquenessCheckService.isNameUsed(this, name),
                "名称({0})已被使用",
                name
        );

        this.name = name;

        updated();
    }

    public void delayRegistrationTo(LocalDateTime newDeadline) {
        CheckUtils.isTrue(
                !ActivityStatusType.ENDED.equals(registrationStatus),
                new IllegalStateException("活动报名已结束不支持延迟")
        );
        CheckUtils.notNull(newDeadline, "活动报名截止日期未提供(newDeadline 必须不为 null)");
        CheckUtils.isTrue(
                registrationTimeRange.getEnd().isBefore(newDeadline),
                "活动报名截至日期无效(newDeadline({0}) 早于当前报名结束时间({1}))",
                DateTimeUtilsEnhance.format(newDeadline),
                DateTimeUtilsEnhance.format(registrationTimeRange.getEnd())
        );

        TimeRange newRegistrationTimeRange = registrationTimeRange.updateEnd(newDeadline);
        checkRegistrationAndParticipationTimes(newRegistrationTimeRange, participationTimeRange);
        registrationTimeRange = newRegistrationTimeRange;

        updated();
    }

    public void delayParticipationTo(LocalDateTime newDeadline) {
        CheckUtils.isTrue(
                !ActivityStatusType.ENDED.equals(participationStatus),
                new IllegalStateException("活动参与已结束不支持延迟")
        );
        CheckUtils.notNull(newDeadline, "活动参与截止日期未提供(newDeadline 必须不为 null)");
        CheckUtils.isTrue(
                participationTimeRange.getEnd().isBefore(newDeadline),
                "",
                "活动参与截至日期无效(newDeadline({0}) 早于当前参与结束时间({1}))",
                DateTimeUtilsEnhance.format(newDeadline),
                DateTimeUtilsEnhance.format(participationTimeRange.getEnd())
        );

        TimeRange newParticipationTimeRange = participationTimeRange.updateEnd(newDeadline);
        participationTimeRange = newParticipationTimeRange;

        updated();
    }

    public void startRegistration() {
        if (ActivityStatusType.ENDED.equals(registrationStatus)) {
            throw new IllegalStateException("活动报名已结束");
        }

        if (ActivityStatusType.PENDING.equals(registrationStatus)) {
            if (!registrationTimeRange.inProgress()) {
                throw new IllegalStateException("当前时间不在活动报名时间范围内");
            }

            registrationStatus = ActivityStatusType.STARTED;

            DomainEventPublisher.instance().publish(new ActivityRegistrationStarted(
                    dealerId.getId(),
                    id.getId(),
                    DateTimeUtilsEnhance.epochMilliSecond()
            ));
        }
    }

    public void stopRegistration() {
        if (ActivityStatusType.PENDING.equals(registrationStatus)) {
            throw new IllegalStateException("活动报名未开始");
        }

        if (ActivityStatusType.STARTED.equals(registrationStatus)) {
            if (!registrationTimeRange.getEnd().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("当前时间未到活动报名结束时间");
            }

            registrationStatus = ActivityStatusType.ENDED;

            DomainEventPublisher.instance().publish(new ActivityRegistrationEnded(
                    dealerId.getId(),
                    id.getId(),
                    DateTimeUtilsEnhance.epochMilliSecond()
            ));
        }
    }

    public void startParticipation() {
        if (ActivityStatusType.ENDED.equals(participationStatus)) {
            throw new IllegalStateException("活动参与已结束");
        }

        if (ActivityStatusType.PENDING.equals(participationStatus)) {
            if (!participationTimeRange.inProgress()) {
                throw new IllegalStateException("当前时间不在活动参与时间范围内");
            }

            participationStatus = ActivityStatusType.STARTED;

            DomainEventPublisher.instance().publish(new ActivityParticipationStarted(
                    dealerId.getId(),
                    id.getId(),
                    DateTimeUtilsEnhance.epochMilliSecond()
            ));
        }
    }

    public void stopParticipation() {
        if (ActivityStatusType.PENDING.equals(participationStatus)) {
            throw new IllegalStateException("活动参与未开始");
        }

        if (ActivityStatusType.STARTED.equals(participationStatus)) {
            if (!registrationTimeRange.getEnd().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("当前时间未到活动参与结束时间");
            }

            participationStatus = ActivityStatusType.ENDED;

            DomainEventPublisher.instance().publish(new ActivityParticipationEnded(
                    dealerId.getId(),
                    id.getId(),
                    DateTimeUtilsEnhance.epochMilliSecond()
            ));
        }
    }

    public ActivityRegistration register(
            Participant participant,
            ActivityRegistrationService registrationLimitService) {
        CheckUtils.notNull(participant, "participant 必须不为 null");

        CheckUtils.isTrue(
                ActivityStatusType.STARTED.equals(registrationStatus),
                "活动暂未开启报名"
        );

        boolean restricted = registrationLimitService.isRegistered(this, participant);
        if (restricted) {
            throw new IllegalStateException("用户报名活动次数已达到限制");
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
                registrationStatus,
                participationStatus,
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
            TimeRange registrationTimeRange,
            TimeRange participationTimeRange,
            int participantLimit,
            Map<GiftId, Integer> gifts) {
        CheckUtils.notBlank(name, "活动名称无效(name 必须不为空)");
        CheckUtils.notBlank(summary, "活动描述无效(summary 必须不为空)");
        CheckUtils.notBlank(image, "活动图片无效(image 必须不为空)");
        CheckUtils.notNull(registrationTimeRange, "活动报名时间无效(registrationTimeRange 必须不为 null)");
        CheckUtils.notNull(participationTimeRange, "活动参与时间无效(participationTimeRange 必须不为 null)");
        CheckUtils.moreThan(participantLimit, 0, "活动参数人数限制无效(participantLimit 必须大于 0)");
        CheckUtils.notEmpty(gifts, "活动礼品无效(gifts 必须不为空)");
        checkRegistrationAndParticipationTimes(registrationTimeRange, participationTimeRange);
    }

    private void checkRegistrationAndParticipationTimes(TimeRange registrationTimeRange, TimeRange participationTimeRange) {
        CheckUtils.isTrue(
                registrationTimeRange.getEnd().isBefore(participationTimeRange.getBegin()),
                "活动报名结束时间({0})应该早于活动参与开始时间({1})",
                DateTimeUtilsEnhance.format(registrationTimeRange.getEnd()),
                DateTimeUtilsEnhance.format(participationTimeRange.getBegin())
        );
    }
}
