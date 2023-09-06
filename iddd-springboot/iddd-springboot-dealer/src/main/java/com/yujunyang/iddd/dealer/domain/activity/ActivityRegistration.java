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

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;

public class ActivityRegistration {
    private ActivityRegistrationId id;
    private DealerId dealerId;
    private ActivityId activityId;
    private Participant participant;
    private LocalDateTime registrationTime;
    private TimeRange usableTimeRange;
    private String verificationCode;
    private ActivityRegistrationStatusType status;
    private LocalDateTime useTime;

    public ActivityRegistration(
            ActivityRegistrationId id,
            DealerId dealerId,
            ActivityId activityId,
            Participant participant,
            LocalDateTime registrationTime,
            TimeRange usableTimeRange,
            String verificationCode,
            ActivityRegistrationStatusType status,
            LocalDateTime useTime) {
        this.id = id;
        this.dealerId = dealerId;
        this.activityId = activityId;
        this.participant = participant;
        this.registrationTime = registrationTime;
        this.usableTimeRange = usableTimeRange;
        this.verificationCode = verificationCode;
        this.status = status;
        this.useTime = useTime;
    }

    public ActivityRegistration(
            ActivityRegistrationId id,
            DealerId dealerId,
            ActivityId activityId,
            Participant participant,
            LocalDateTime registrationTime,
            TimeRange usableTimeRange) {
        this(
                id,
                dealerId,
                activityId,
                participant,
                registrationTime,
                usableTimeRange,
                "",
                ActivityRegistrationStatusType.VERIFICATION_CODE_GENERATING,
                null
        );

        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.notNull(dealerId, "dealerId 必须不为 null");
        CheckUtils.notNull(activityId, "activityId 必须不为 null");
        CheckUtils.notNull(participant, "participant 必须不为 null");
        CheckUtils.notNull(registrationTime, "registrationTime 必须不为 null");
        CheckUtils.notNull(usableTimeRange, "usableTimeRange 必须不为 null");

        DomainEventPublisher.instance().publish(new ActivityRegistrationCreated(
                dealerId.getId(),
                activityId.getId(),
                id.getId(),
                DateTimeUtilsEnhance.epochMilliSecond()
        ));
    }

    public void generateVerificationCode() {

    }

    public ActivityRegistrationId getId() {
        return id;
    }

    public DealerId getDealerId() {
        return dealerId;
    }

    public ActivityId getActivityId() {
        return activityId;
    }

    public Participant getParticipant() {
        return participant;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public TimeRange getUsableTimeRange() {
        return usableTimeRange;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public ActivityRegistrationStatusType getStatus() {
        return status;
    }

    public LocalDateTime getUseTime() {
        return useTime;
    }
}
