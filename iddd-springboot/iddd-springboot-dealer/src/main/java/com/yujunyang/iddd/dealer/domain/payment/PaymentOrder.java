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
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.order.OrderType;

public class PaymentOrder {
    private PaymentOrderId id;
    private OrderType orderType;
    private AbstractLongId orderId;
    private PaymentChannelType paymentChannelType;
    private PaymentMethodType paymentMethodType;
    private LocalDateTime createTime;
    private String description;
    private int amount;
    protected PaymentStatusType status;
    protected String outTradeNo;
    private Map<String, Object> paymentParams;
    private Map<String, Object> paymentDetails;


    public PaymentOrder(
            PaymentOrderId id,
            OrderType orderType,
            AbstractLongId orderId,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            LocalDateTime createTime,
            String description,
            int amount,
            PaymentStatusType status,
            String outTradeNo,
            Map<String, Object> paymentParams,
            Map<String, Object> paymentDetails) {
        this.id = id;
        this.orderType = orderType;
        this.orderId = orderId;
        this.paymentChannelType = paymentChannelType;
        this.paymentMethodType = paymentMethodType;
        this.createTime = createTime;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.outTradeNo = outTradeNo;
        this.paymentParams = paymentParams;
        this.paymentDetails = paymentDetails;
    }

    public PaymentOrder(
            PaymentOrderId id,
            OrderType orderType,
            AbstractLongId orderId,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            String description,
            int amount,
            Map<String, Object> paymentParams) {
        this(
                id,
                orderType,
                orderId,
                paymentChannelType,
                paymentMethodType,
                LocalDateTime.now(),
                description,
                amount,
                PaymentStatusType.NOT_INITIATED,
                id.toString(),
                paymentParams,
                null);
    }

    public InitiatePaymentResult initiatePayment(
            PaymentService paymentService) {
        CheckUtils.isTrue(
                PaymentStatusType.NOT_INITIATED.equals(status),
                new BusinessRuleException(
                        "支付订单状态不支持发起支付",
                        ImmutableMap.of(
                                "paymentOrderId",
                                id.getId(),
                                "status",
                                status
                        )
                )
        );

        InitiatePaymentResult initiatePaymentResult = paymentService.initiatePayment(this);

        status = PaymentStatusType.INITIATED;

        DomainEventPublisher.instance().publish(new PaymentInitiated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                paymentChannelType,
                paymentMethodType,
                id.getId(),
                orderType,
                orderId.getId()
        ));

        return initiatePaymentResult;
    }

    public void markAsPaymentSuccess() {
        CheckUtils.isTrue(
                PaymentStatusType.INITIATED.equals(status),
                new BusinessRuleException(
                        "支付订单状态不支持设置为支付成功",
                        ImmutableMap.of(
                                "paymentOrderId",
                                id.getId(),
                                "status",
                                status
                        )
                )
        );

        status = PaymentStatusType.PAID;

        DomainEventPublisher.instance().publish(new PaymentPaid(
                DateTimeUtilsEnhance.epochMilliSecond(),
                paymentChannelType,
                paymentMethodType,
                id.getId(),
                orderType,
                orderId.getId()
        ));
    }

    public PaymentChannelType paymentChannelType() {
        return paymentChannelType;
    }

    public PaymentMethodType paymentMethodType() {
        return paymentMethodType;
    }

    public PaymentOrderId id() {
        return id;
    }

    public PaymentStatusType status() {
        return status;
    }

    public AbstractLongId orderId() {
        return orderId;
    }

    public OrderType orderType() {
        return orderType;
    }

    public void syncPaymentResult(PaymentService paymentService) {
        CheckUtils.isTrue(
                PaymentStatusType.INITIATED.equals(status),
                new BusinessRuleException(
                        "支付单非已发起支付状态,不支持同步支付结果",
                        ImmutableMap.of(
                                "paymentOrderId",
                                id.getId(),
                                "status",
                                status
                        )
                )
        );

        PaymentResult paymentResult = paymentService.queryPaymentStatus(this);
        List<PaymentStatusType> needSyncPaymentStatusList = Arrays.asList(
                PaymentStatusType.PAID,
                PaymentStatusType.FAILED
        );
        CheckUtils.isTrue(
                needSyncPaymentStatusList.contains(paymentResult.status()),
                new BusinessRuleException(
                        "支付单查询结果状态非已支付或支付失败,暂不同步结果",
                        ImmutableMap.of(
                                "paymentOrderId",
                                id.getId(),
                                "paymentResultStatus",
                                paymentResult.status()
                        )
                )
        );

        status = paymentResult.status();
        paymentDetails = paymentResult.details();

        if (PaymentStatusType.PAID.equals(paymentResult.status())) {
            DomainEventPublisher.instance().publish(new PaymentPaid(
                    DateTimeUtilsEnhance.epochMilliSecond(),
                    paymentChannelType,
                    paymentMethodType,
                    id.getId(),
                    orderType,
                    orderId.getId()
            ));
        } else if (PaymentStatusType.FAILED.equals(paymentResult.status())) {
            DomainEventPublisher.instance().publish(new PaymentPaid(
                    DateTimeUtilsEnhance.epochMilliSecond(),
                    paymentChannelType,
                    paymentMethodType,
                    id.getId(),
                    orderType,
                    orderId.getId()
            ));
        }
    }
}
