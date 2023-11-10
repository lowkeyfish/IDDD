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

package com.yujunyang.iddd.dealer.domain.order;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.RefundRequested;
import com.yujunyang.iddd.dealer.domain.payment.RefundRequestedDueToRepeatedPayment;

public abstract class AbstractOrder {
    protected AbstractLongId id;
    protected OrderStatusType status;
    protected LocalDateTime createTime;
    protected int amount;
    protected PaymentOrderId paymentOrderId;

    protected AbstractOrder(
            AbstractLongId id,
            OrderStatusType status,
            LocalDateTime createTime,
            int amount) {
        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.isTrue(amount >= 0, "amount 必须不小于 0");
        CheckUtils.notNull(status, "status 必须不为 null");
        CheckUtils.notNull(createTime, "createTime 必须不为 null");

        this.id = id;
        this.status = status;
        this.createTime = createTime;
        this.amount = amount;
    }

    public void markAsPaid(PaymentOrderId paymentOrderId) {
        if (OrderStatusType.PAID.equals(status)) {
            if (!this.paymentOrderId.equals(paymentOrderId)) {
                DomainEventPublisher.instance().publish(new RefundRequestedDueToRepeatedPayment(
                        DateTimeUtilsEnhance.epochMilliSecond(),
                        paymentOrderId.getId(),
                        orderType(),
                        id.getId()
                ));
            }
            return;
        }

        CheckUtils.isTrue(
                OrderStatusType.PAYMENT_INITIATED.equals(status),
                new BusinessRuleException(
                        "订单当前状态不能变更为已支付",
                        ImmutableMap.of(
                                "orderId",
                                id.getId(),
                                "orderType",
                                orderType(),
                                "status",
                                status
                        )
                )
        );

        this.paymentOrderId = paymentOrderId;
        status = OrderStatusType.PAID;

        DomainEventPublisher.instance().publish(new OrderPaid(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId(),
                orderType()
        ));
    }

    public void markAsPaymentInitiated() {
        CheckUtils.isTrue(
                Arrays.asList(
                        OrderStatusType.PAYMENT_NOT_INITIATED,
                        OrderStatusType.PAYMENT_INITIATED,
                        OrderStatusType.PAYMENT_FAILED
                ).contains(status),
                new BusinessRuleException(
                        "订单当前状态不能变更为已发起支付",
                        ImmutableMap.of(
                                "orderId",
                                id.getId(),
                                "orderType",
                                orderType(),
                                "status",
                                status
                        )
                )
        );

        status = OrderStatusType.PAYMENT_INITIATED;

        DomainEventPublisher.instance().publish(new OrderPaymentInitiated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId(),
                orderType()
        ));
    }

    public void markAsFailed() {
        if (OrderStatusType.PAYMENT_FAILED.equals(status)) {
            return;
        }

        CheckUtils.isTrue(
                OrderStatusType.PAYMENT_INITIATED.equals(status),
                new BusinessRuleException(
                        "订单当前状态不能变更为支付失败",
                        ImmutableMap.of(
                                "orderId",
                                id.getId(),
                                "orderType",
                                orderType(),
                                "status",
                                status
                        )
                )
        );

        status = OrderStatusType.PAYMENT_FAILED;

        DomainEventPublisher.instance().publish(new OrderPaymentFailed(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId(),
                orderType()
        ));
    }

    public OrderStatusType status() {
        return status;
    }

    public AbstractLongId id() {
        return id;
    }

    public int amount() {
        return amount;
    }

    public PaymentOrderId paymentOrderId() {
        return paymentOrderId;
    }

    public abstract OrderType orderType();


}
