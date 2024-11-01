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
import java.util.Date;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStatusType;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrder;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrderId;
import com.yujunyang.iddd.dealer.domain.payment.RefundReasonType;
import com.yujunyang.iddd.dealer.domain.payment.RefundRequested;
import com.yujunyang.iddd.dealer.domain.payment.RefundStatusType;

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

    public void markAsPaid(PaymentOrder paymentOrder) {
        checkPaymentOrderStatus(
                paymentOrder,
                PaymentStatusType.PAID,
                "订单状态不能设置为已支付,因为支付单当前状态非已支付");
        checkOrderAndPaymentOrderRelation(
                paymentOrder,
                "订单状态不能设置为已支付,因为支付单关联订单和当前订单不匹配");

        if (OrderStatusType.PAID.equals(status)) {
            if (!paymentOrderId.equals(paymentOrder.id())) {
                DomainEventPublisher.instance().publish(new RefundRequested(
                        DateTimeUtilsEnhance.epochMilliSecond(),
                        paymentOrder.id().getId(),
                        orderType(),
                        id.getId(),
                        RefundReasonType.PAYMENT_REPEATED
                ));
            }
            return;
        }

        CheckUtils.isTrue(
                Arrays.asList(
                        OrderStatusType.PAYMENT_INITIATED,
                        // 可能支付单已支付早于订单设置已发起支付, 因此未发起支付允许变更为已支付
                        OrderStatusType.PAYMENT_NOT_INITIATED
                ).contains(status),
                new BusinessRuleException(
                        "订单状态不能设置为已支付,因为当前状态非未发起支付或已发起支付",
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

        paymentOrderId = paymentOrder.id();
        status = OrderStatusType.PAID;

        DomainEventPublisher.instance().publish(new OrderPaid(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId(),
                orderType(),
                paymentOrder.id().getId()
        ));
    }

    public void markAsPaymentInitiated(PaymentOrder paymentOrder) {
        checkPaymentOrderStatus(
                paymentOrder,
                PaymentStatusType.INITIATED,
                "订单状态不能设置为已发起支付,因为支付单状态非已发起支付");
        checkOrderAndPaymentOrderRelation(
                paymentOrder,
                "订单状态不能设置为已发起支付,因为支付单关联订单和当前订单不匹配");

        if (OrderStatusType.PAYMENT_INITIATED.equals(status)) {
            return;
        }

        CheckUtils.isTrue(
                Arrays.asList(
                        OrderStatusType.PAYMENT_NOT_INITIATED
                ).contains(status),
                new BusinessRuleException(
                        "订单状态不能设置为已发起支付,因为订单当前状态非未发起支付",
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
                orderType(),
                paymentOrder.id().getId()
        ));
    }

    public void markAsPaymentFailed(PaymentOrder paymentOrder) {
        checkPaymentOrderStatus(
                paymentOrder,
                PaymentStatusType.FAILED,
                "订单状态不能设置为支付失败,因为支付单状态非支付失败");
        checkOrderAndPaymentOrderRelation(
                paymentOrder,
                "订单状态不能设置为支付失败,因为支付单关联订单和当前订单不匹配");

        if (OrderStatusType.PAYMENT_FAILED.equals(status)) {
            return;
        }

        CheckUtils.isTrue(
                Arrays.asList(
                        OrderStatusType.PAYMENT_NOT_INITIATED,
                        OrderStatusType.PAYMENT_INITIATED
                ).contains(status),
                new BusinessRuleException(
                        "订单状态不能设置为支付失败,因为订单当前状态非未发起支付或已发起支付",
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
                orderType(),
                paymentOrder.id().getId()
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

    private void checkPaymentOrderStatus(
            PaymentOrder paymentOrder,
            PaymentStatusType expectedPaymentStatusType,
            String errorMessage) {
        CheckUtils.notNull(paymentOrder, "paymentOrder 必须不为 null");
        CheckUtils.notNull(expectedPaymentStatusType, "expectedPaymentStatusType 必须不为 null");
        CheckUtils.notBlank(errorMessage, "errorMessage 必须不为空");

        CheckUtils.isTrue(
                expectedPaymentStatusType.equals(paymentOrder.status()),
                new BusinessRuleException(
                        errorMessage,
                        ImmutableMap.of(
                                "orderId",
                                id.getId(),
                                "orderType",
                                orderType(),
                                "paymentOrderId",
                                paymentOrder.id().getId(),
                                "paymentOrderStatus",
                                paymentOrder.status()
                        )
                )
        );
    }

    private void checkOrderAndPaymentOrderRelation(PaymentOrder paymentOrder, String errorMessage) {
        CheckUtils.notNull(paymentOrder, "paymentOrder 必须不为 null");

        CheckUtils.isTrue(
                paymentOrder.orderType().equals(orderType())
                        && id.getId().equals(paymentOrder.orderId().getId()),
                new BusinessRuleException(
                        errorMessage,
                        ImmutableMap.of(
                                "orderId",
                                id.getId(),
                                "orderType",
                                orderType(),
                                "paymentOrderId",
                                paymentOrder.id().getId(),
                                "paymentOrder.orderType",
                                paymentOrder.orderType(),
                                "paymentOrder.orderId",
                                paymentOrder.orderId().getId()
                        )
                )
        );
    }

    public void requestRefund() {
        if (OrderStatusType.REFUND_REQUESTED.equals(status)) {
            return;
        }

        CheckUtils.isTrue(
                PaymentStatusType.PAID.equals(status),
                new BusinessRuleException(
                        "订单不能发起退款,因为当前状态非已支付",
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

        status = OrderStatusType.REFUND_REQUESTED;

        DomainEventPublisher.instance().publish(new RefundRequested(
                DateTimeUtilsEnhance.epochMilliSecond(),
                paymentOrderId().getId(),
                orderType(),
                id.getId(),
                RefundReasonType.USER_REQUEST
        ));
    }

    public void markAsRefundInitiated(RefundOrder refundOrder) {
        checkRefundOrderStatus(
                refundOrder,
                RefundStatusType.INITIATED,
                "订单状态不能设置为已发起退款,因为退款单状态非已发起退款");
        checkOrderAndRefundOrderRelation(
                refundOrder,
                "订单状态不能设置为已发起退款,因为退款单关联订单和当前订单不匹配"
        );

        if (OrderStatusType.REFUND_INITIATED.equals(status)) {
            return;
        }

        CheckUtils.isTrue(
                OrderStatusType.REFUND_REQUESTED.equals(status),
                new BusinessRuleException(
                        "订单状态不能设置为已发起退款,因为当前状态非已申请退款",
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

        status = OrderStatusType.REFUND_INITIATED;
        DomainEventPublisher.instance().publish(new OrderRefundInitiated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId(),
                orderType(),
                refundOrder.paymentOrderId().getId(),
                refundOrder.id().getId()
        ));
    }

    public void markAsRefunded(RefundOrder refundOrder) {
        checkRefundOrderStatus(
                refundOrder,
                RefundStatusType.REFUNDED,
                "订单状态不能设置为已退款,因为退款单状态非已退款");
        checkOrderAndRefundOrderRelation(
                refundOrder,
                "订单状态不能设置为已退款,因为退款单关联订单和当前订单不匹配"
        );

        if (OrderStatusType.REFUNDED.equals(status)) {
            return;
        }

        CheckUtils.isTrue(
                Arrays.asList(
                        OrderStatusType.REFUND_REQUESTED,
                        OrderStatusType.REFUND_INITIATED
                ).contains(status),
                new BusinessRuleException(
                        "订单状态不能设置为已退款,因为当前状态非已申请退款或已发起退款",
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

        status = OrderStatusType.REFUNDED;

        DomainEventPublisher.instance().publish(new OrderRefunded(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId(),
                orderType(),
                paymentOrderId().getId(),
                refundOrder.id().getId()
        ));
    }

    private void checkRefundOrderStatus(
            RefundOrder refundOrder,
            RefundStatusType expectedRefundStatusType,
            String errorMessage) {
        CheckUtils.notNull(refundOrder, "refundOrder 必须不为 null");
        CheckUtils.notNull(expectedRefundStatusType, "expectedRefundStatusType 必须不为 null");
        CheckUtils.notBlank(errorMessage, "errorMessage 必须不为空");

        CheckUtils.isTrue(
                expectedRefundStatusType.equals(refundOrder.status()),
                new BusinessRuleException(
                        errorMessage,
                        ImmutableMap.of(
                                "orderId",
                                id.getId(),
                                "orderType",
                                orderType(),
                                "paymentOrderId",
                                refundOrder.paymentOrderId().getId(),
                                "refundOrderId",
                                refundOrder.id().getId(),
                                "refundOrderStatus",
                                refundOrder.status()
                        )
                )
        );
    }

    private void checkOrderAndRefundOrderRelation(
            RefundOrder refundOrder,
            String errorMessage) {
        CheckUtils.notNull(refundOrder, "refundOrder 必须不为 null");

        CheckUtils.isTrue(
                refundOrder.orderType().equals(orderType())
                        && id.getId().equals(refundOrder.orderId().getId()),
                new BusinessRuleException(
                        errorMessage,
                        ImmutableMap.of(
                                "orderId",
                                id.getId(),
                                "orderType",
                                orderType(),
                                "paymentOrderId",
                                refundOrder.paymentOrderId().getId(),
                                "refundOrderId",
                                refundOrder.id().getId(),
                                "refundOrder.orderType",
                                refundOrder.orderType(),
                                "refundOrder.orderId",
                                refundOrder.orderId().getId()
                        )
                )
        );
    }
}
