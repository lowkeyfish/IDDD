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
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;

public class PaymentOrder {
    private PaymentOrderId id;
    private PaymentScenarioType paymentScenarioType;
    private AbstractLongId scenarioRelationId;
    private PaymentChannelType paymentChannelType;
    private PaymentMethodType paymentMethodType;
    private LocalDateTime createTime;
    private String description;
    private int amount;
    protected PaymentStatusType status;
    protected String outTradeNo;
    private Map<String, Object> paymentChannelParams;

    public PaymentOrder(
            PaymentOrderId id,
            PaymentScenarioType paymentScenarioType,
            AbstractLongId scenarioRelationId,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            LocalDateTime createTime,
            String description,
            int amount,
            PaymentStatusType status,
            String outTradeNo,
            Map<String, Object> paymentChannelParams) {
        this.id = id;
        this.paymentScenarioType = paymentScenarioType;
        this.scenarioRelationId = scenarioRelationId;
        this.paymentChannelType = paymentChannelType;
        this.paymentMethodType = paymentMethodType;
        this.createTime = createTime;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.outTradeNo = outTradeNo;
        this.paymentChannelParams = paymentChannelParams;
    }

    public PaymentOrder(
            PaymentOrderId id,
            PaymentScenarioType paymentScenarioType,
            AbstractLongId scenarioRelationId,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            String description,
            int amount,
            Map<String, Object> paymentChannelParams) {
        this(
                id,
                paymentScenarioType,
                scenarioRelationId,
                paymentChannelType,
                paymentMethodType,
                LocalDateTime.now(),
                description,
                amount,
                PaymentStatusType.NOT_INITIATED,
                id.toString(),
                paymentChannelParams
        );
    }

    public InitiatePaymentResult initiatePayment(
            PaymentService paymentService) {
        CheckUtils.isTrue(
                PaymentStatusType.NOT_INITIATED.equals(status),
                new BusinessRuleException(
                        "支付订单状态不支持发起支付",
                        ImmutableMap.of(
                                "id",
                                id.getId(),
                                "status",
                                status
                        )
                )
        );

        InitiatePaymentResult initiatePaymentResult = paymentService.initiatePayment(this);

        DomainEventPublisher.instance().publish(new PaymentInitiated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));

        return initiatePaymentResult;
    }

    public void handlePaymentSuccess() {
        CheckUtils.isTrue(
                PaymentStatusType.INITIATED.equals(status),
                new BusinessRuleException(
                        "支付订单状态不支持设置为支付成功",
                        ImmutableMap.of(
                                "id",
                                id.getId(),
                                "status",
                                status
                        )
                )
        );

        status = PaymentStatusType.SUCCESS;

        DomainEventPublisher.instance().publish(new PaymentSucceeded(
                DateTimeUtilsEnhance.epochMilliSecond(),
                paymentChannelType,
                id.getId()
        ));
    }

    public PaymentChannelType paymentChannelType() {
        return paymentChannelType;
    }

    public PaymentOrderId id() {
        return id;
    }


}
