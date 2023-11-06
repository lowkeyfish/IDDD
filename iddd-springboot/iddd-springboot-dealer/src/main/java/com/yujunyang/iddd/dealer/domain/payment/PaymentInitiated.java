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

package com.yujunyang.iddd.dealer.domain.payment;

import java.text.MessageFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yujunyang.iddd.common.domain.event.DomainEvent;

public class PaymentInitiated implements DomainEvent {
    private long timestamp;
    private long paymentOrderId;

    @JsonCreator
    public PaymentInitiated(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("paymentOrderId") long paymentOrderId) {
        this.timestamp = timestamp;
        this.paymentOrderId = paymentOrderId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public long getPaymentOrderId() {
        return paymentOrderId;
    }

    @Override
    public String eventKey() {
        return MessageFormat.format(
                "PaymentOrderId({1,number,#})",
                paymentOrderId
        );
    }

    @Override
    public String notificationRoutingKey() {
        return "Payment." + notificationType();
    }
}
