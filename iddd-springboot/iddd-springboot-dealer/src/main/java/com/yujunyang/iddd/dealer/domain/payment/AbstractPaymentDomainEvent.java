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

import com.yujunyang.iddd.common.domain.event.DomainEvent;

public class AbstractPaymentDomainEvent implements DomainEvent {
    private long timestamp;
    private PaymentChannelType paymentChannelType;
    private PaymentMethodType paymentMethodType;
    private long paymentOrderId;
    private PaymentScenarioType paymentScenarioType;
    private long scenarioRelationId;

    protected AbstractPaymentDomainEvent(
            long timestamp,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            long paymentOrderId,
            PaymentScenarioType paymentScenarioType,
            long scenarioRelationId) {
        this.timestamp = timestamp;
        this.paymentChannelType = paymentChannelType;
        this.paymentMethodType = paymentMethodType;
        this.paymentOrderId = paymentOrderId;
        this.paymentScenarioType = paymentScenarioType;
        this.scenarioRelationId = scenarioRelationId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public long getPaymentOrderId() {
        return paymentOrderId;
    }

    public PaymentScenarioType getPaymentScenarioType() {
        return paymentScenarioType;
    }

    public long getScenarioRelationId() {
        return scenarioRelationId;
    }

    public PaymentChannelType getPaymentChannelType() {
        return paymentChannelType;
    }

    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    @Override
    public String eventKey() {
        return MessageFormat.format(
                "PaymentOrderId({0,number,#})",
                paymentOrderId
        );
    }

    @Override
    public String notificationRoutingKey() {
        return "Payment." + notificationType();
    }
}