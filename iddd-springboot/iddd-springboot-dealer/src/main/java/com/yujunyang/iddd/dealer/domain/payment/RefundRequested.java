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
import com.yujunyang.iddd.dealer.domain.order.OrderType;

public class RefundRequested implements DomainEvent {
    private long timestamp;
    private long paymentOrderId;
    private OrderType orderType;
    private long orderId;
    private RefundReasonType refundReasonType;


    @JsonCreator
    public RefundRequested(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("paymentOrderId") long paymentOrderId,
            @JsonProperty("orderType") OrderType orderType,
            @JsonProperty("orderId") long orderId,
            @JsonProperty("refundReasonType") RefundReasonType refundReasonType) {
        this.timestamp = timestamp;
        this.paymentOrderId = paymentOrderId;
        this.orderType = orderType;
        this.orderId = orderId;
        this.refundReasonType = refundReasonType;
    }


    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public long getPaymentOrderId() {
        return paymentOrderId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public long getOrderId() {
        return orderId;
    }

    public RefundReasonType getRefundReasonType() {
        return refundReasonType;
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
