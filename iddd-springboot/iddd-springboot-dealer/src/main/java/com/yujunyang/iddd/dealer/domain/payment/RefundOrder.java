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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.order.OrderType;

public class RefundOrder {
    private RefundOrderId id;
    private PaymentOrderId paymentOrderId;
    private OrderType orderType;
    private AbstractLongId orderId;
    private RefundStatusType status;
    private PaymentChannelType paymentChannelType;
    private LocalDateTime createTime;
    private int amount;
    private String outTradeNo;
    private String outRefundNo;
    private Map<String, Object> refundDetails;
    private RefundReasonType refundReasonType;

    public RefundOrder(
            RefundOrderId id,
            PaymentOrderId paymentOrderId,
            OrderType orderType,
            AbstractLongId orderId,
            RefundStatusType status,
            PaymentChannelType paymentChannelType,
            RefundReasonType refundReasonType,
            LocalDateTime createTime,
            int amount,
            String outTradeNo,
            String outRefundNo,
            Map<String, Object> refundDetails) {
        this.id = id;
        this.paymentOrderId = paymentOrderId;
        this.orderType = orderType;
        this.orderId = orderId;
        this.status = status;
        this.paymentChannelType = paymentChannelType;
        this.createTime = createTime;
        this.amount = amount;
        this.outTradeNo = outTradeNo;
        this.outRefundNo = outRefundNo;
        this.refundDetails = refundDetails;
        this.refundReasonType = refundReasonType;
    }

    public RefundOrder(
            RefundOrderId id,
            PaymentOrderId paymentOrderId,
            OrderType orderType,
            AbstractLongId orderId,
            PaymentChannelType paymentChannelType,
            int amount,
            String outTradeNo,
            RefundReasonType refundReasonType) {
        this(
                id,
                paymentOrderId,
                orderType,
                orderId,
                RefundStatusType.NOT_INITIATED,
                paymentChannelType,
                refundReasonType,
                LocalDateTime.now(),
                amount,
                outTradeNo,
                id.toString(),
                null
        );
    }

    public void initiateRefund(PaymentService paymentService) {
        if (RefundStatusType.INITIATED.equals(status)) {
            return;
        }

        CheckUtils.isTrue(
                RefundStatusType.NOT_INITIATED.equals(status),
                new BusinessRuleException(
                        "退款单不能发起退款,因为当前状态非未发起退款",
                        ImmutableMap.of(
                            "refundOrderId",
                                id.getId(),
                                "status",
                                status
                        )
                )
        );

        paymentService.initiateRefund(this);

        status = RefundStatusType.INITIATED;

        DomainEventPublisher.instance().publish(new RefundInitiated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                paymentChannelType,
                paymentOrderId.getId(),
                orderType,
                orderId.getId(),
                id.getId()
        ));
    }

    public void syncRefundResult(PaymentService paymentService) {
        if (!Arrays.asList(
                RefundStatusType.NOT_INITIATED,
                RefundStatusType.INITIATED
        ).contains(status)) {
            return;
        }

        RefundResult refundResult = paymentService.queryRefundStatus(this);
        if (!Arrays.asList(
                RefundStatusType.REFUNDED,
                RefundStatusType.FAILED
        ).contains(refundResult.status())) {
            return;
        }

        status = refundResult.status();
        refundDetails = refundResult.details();

        if (RefundStatusType.REFUNDED.equals(refundResult.status())) {
            DomainEventPublisher.instance().publish(new Refunded(
                    DateTimeUtilsEnhance.epochMilliSecond(),
                    paymentChannelType,
                    paymentOrderId.getId(),
                    orderType,
                    orderId.getId(),
                    id.getId()
            ));
        } else if (RefundStatusType.FAILED.equals(refundResult.status())) {
            DomainEventPublisher.instance().publish(new RefundFailed(
                    DateTimeUtilsEnhance.epochMilliSecond(),
                    paymentChannelType,
                    paymentOrderId.getId(),
                    orderType,
                    orderId.getId(),
                    id.getId()
            ));
        }
    }

    public RefundOrderId id() {
        return id;
    }

    public PaymentOrderId paymentOrderId() {
        return paymentOrderId;
    }

    public OrderType orderType() {
        return orderType;
    }

    public AbstractLongId orderId() {
        return orderId;
    }

    public RefundStatusType status() {
        return status;
    }

    public PaymentChannelType paymentChannelType() {
        return paymentChannelType;
    }


}
