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

package com.yujunyang.iddd.dealer.domain.sms;

import java.time.LocalDateTime;

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;

public class Sms {
    private SmsId id;
    private String mobile;
    private String message;
    private SmsSendStatus sendStatus;
    private LocalDateTime requestTime;
    private LocalDateTime sentTime;
    private String from;
    private String fromData;

    public Sms(
            SmsId id,
            String mobile,
            String message,
            SmsSendStatus sendStatus,
            LocalDateTime requestTime,
            LocalDateTime sentTime,
            String from,
            String fromData) {
        this.id = id;
        this.mobile = mobile;
        this.message = message;
        this.sendStatus = sendStatus;
        this.requestTime = requestTime;
        this.sentTime = sentTime;
        this.from = from;
        this.fromData = fromData;
    }

    public Sms(
            SmsId id,
            String mobile,
            String message,
            String from,
            String fromData) {
        this(id, mobile, message, SmsSendStatus.UNSENT, LocalDateTime.now(), null, from, fromData);

        DomainEventPublisher.instance().publish(new SmsCreated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public String mobile() {
        return mobile;
    }

    public String message() {
        return message;
    }

    public SmsSendStatus sendStatus() {
        return sendStatus;
    }

    public LocalDateTime requestTime() {
        return requestTime;
    }

    public LocalDateTime sendTime() {
        return sentTime;
    }

    public SmsId id() {
        return id;
    }

    public String from() {
        return from;
    }

    public String fromData() {
        return fromData;
    }

    public void send(SmsSendService smsSendService) {
        if (!sendStatus.equals(SmsSendStatus.UNSENT)) {
            return;
        }

        smsSendService.send(this);
        sendStatus = SmsSendStatus.SENT;
        sentTime = LocalDateTime.now();

        DomainEventPublisher.instance().publish(new SmsSent(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }
}
