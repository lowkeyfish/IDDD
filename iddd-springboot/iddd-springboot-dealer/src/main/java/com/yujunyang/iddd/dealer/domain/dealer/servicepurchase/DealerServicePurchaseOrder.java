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

package com.yujunyang.iddd.dealer.domain.dealer.servicepurchase;

import java.time.LocalDateTime;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStatusType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStrategy;

public class DealerServicePurchaseOrder {
    private DealerServicePurchaseOrderId id;
    private DealerId dealerId;
    private TimeRange servicePeriod;
    private DealerServicePurchaseOrderStatusType status;
    private LocalDateTime createTime;
    private int amount;
    private PaymentChannelType paymentChannelType;
    private AbstractLongId paymentOrderId;


    public DealerServicePurchaseOrder(
            DealerServicePurchaseOrderId id,
            DealerId dealerId,
            TimeRange servicePeriod,
            int amount) {
        this(
                id,
                dealerId,
                servicePeriod,
                DealerServicePurchaseOrderStatusType.PAYMENT_NOT_INITIATED,
                LocalDateTime.now(),
                amount
        );

        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.notNull(dealerId, "dealerId 必须不为 null");
        CheckUtils.notNull(servicePeriod, "servicePeriod 必须不为 null");
        CheckUtils.isTrue(amount >= 0, "amount 必须不小于 0");

        DomainEventPublisher.instance().publish(new DealerServicePurchaseOrderCreated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public DealerServicePurchaseOrder(
            DealerServicePurchaseOrderId id,
            DealerId dealerId,
            TimeRange servicePeriod,
            DealerServicePurchaseOrderStatusType status,
            LocalDateTime createTime,
            int amount) {
        this.id = id;
        this.dealerId = dealerId;
        this.servicePeriod = servicePeriod;
        this.status = status;
        this.createTime = createTime;
        this.amount = amount;
    }


    public void completePayment() {

    }

    public void cancel() {

    }

    public String initiatePayment(PaymentStrategy paymentStrategy) {
        CheckUtils.isTrue(
                DealerServicePurchaseOrderStatusType.PAYMENT_NOT_INITIATED.equals(status),
                new BusinessRuleException(
                        "当前订单已发起过支付",
                        ImmutableMap.of(
                                "id",
                                id,
                                "status",
                                status
                        )
                )
        );

        InitiatePaymentResult initiatePaymentResult = paymentStrategy.initiatePayment(
                PaymentScenarioType.DEALER_SERVICE_PURCHASE,
                id,
                amount,
                "服务购买"
        );

        status = DealerServicePurchaseOrderStatusType.PAYMENT_INITIATED;
        paymentChannelType = initiatePaymentResult.getPaymentChannelType();
        paymentOrderId = initiatePaymentResult.getPaymentOrderId();

        return initiatePaymentResult.getPaymentInitiationData().getData();
    }

    public DealerServicePurchaseOrderId id() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public DealerServicePurchaseOrderSnapshot snapshot() {
        return new DealerServicePurchaseOrderSnapshot(
                id,
                dealerId,
                servicePeriod,
                status,
                createTime,
                amount);
    }
}
