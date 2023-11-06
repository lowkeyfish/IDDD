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
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;

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


    public void handleSuccessfulPayment() {
        CheckUtils.isTrue(
                DealerServicePurchaseOrderStatusType.PAYMENT_INITIATED.equals(status),
                new BusinessRuleException(
                        "当前状态不能变更为支付成功",
                        ImmutableMap.of(
                                "id",
                                id,
                                "status",
                                status
                        )
                )
        );

        status = DealerServicePurchaseOrderStatusType.PAYMENT_SUCCESS;

        DomainEventPublisher.instance().publish(new DealerServicePurchaseOrderPaymentSucceeded(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void cancel() {

    }

    public void initiatePayment() {
        List<DealerServicePurchaseOrderStatusType> allowStatusList = Arrays.asList(
                DealerServicePurchaseOrderStatusType.PAYMENT_NOT_INITIATED,
                DealerServicePurchaseOrderStatusType.PAYMENT_INITIATED
        );

        CheckUtils.isTrue(
                allowStatusList.contains(status),
                new BusinessRuleException(
                        "当前订单状态不允许发起支付",
                        ImmutableMap.of(
                                "id",
                                id,
                                "status",
                                status
                        )
                )
        );

        status = DealerServicePurchaseOrderStatusType.PAYMENT_INITIATED;
    }


    public void initiateRefund() {
        CheckUtils.isTrue(
                DealerServicePurchaseOrderStatusType.PAYMENT_SUCCESS.equals(status),
                new BusinessRuleException(
                        "订单状态非支付成功不能申请退款",
                        ImmutableMap.of(
                                "id",
                                id,
                                "status",
                                status
                        )
                )
        );

        status = DealerServicePurchaseOrderStatusType.REFUND_INITIATED;
    }

    public boolean isPaymentInitiated() {
        return DealerServicePurchaseOrderStatusType.PAYMENT_INITIATED.equals(status);
    }

    public DealerServicePurchaseOrderId id() {
        return id;
    }

    public int amount() {
        return amount;
    }

    public AbstractLongId paymentOrderId() {
        return paymentOrderId;
    }

    public PaymentChannelType paymentChannelType() {
        return paymentChannelType;
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
