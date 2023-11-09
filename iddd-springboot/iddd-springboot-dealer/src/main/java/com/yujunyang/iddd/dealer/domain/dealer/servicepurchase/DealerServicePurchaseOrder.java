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

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.order.AbstractOrder;
import com.yujunyang.iddd.dealer.domain.order.OrderCreated;
import com.yujunyang.iddd.dealer.domain.order.OrderStatusType;
import com.yujunyang.iddd.dealer.domain.order.OrderType;

public class DealerServicePurchaseOrder extends AbstractOrder {
    private DealerId dealerId;
    private TimeRange servicePeriod;


    public DealerServicePurchaseOrder(
            DealerServicePurchaseOrderId id,
            DealerId dealerId,
            TimeRange servicePeriod,
            int amount) {
        this(
                id,
                dealerId,
                servicePeriod,
                OrderStatusType.PAYMENT_NOT_INITIATED,
                LocalDateTime.now(),
                amount
        );

        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.notNull(dealerId, "dealerId 必须不为 null");
        CheckUtils.notNull(servicePeriod, "servicePeriod 必须不为 null");
        CheckUtils.isTrue(amount >= 0, "amount 必须不小于 0");

        DomainEventPublisher.instance().publish(new OrderCreated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId(),
                orderType()
        ));
    }

    public DealerServicePurchaseOrder(
            DealerServicePurchaseOrderId id,
            DealerId dealerId,
            TimeRange servicePeriod,
            OrderStatusType status,
            LocalDateTime createTime,
            int amount) {
        super(id, status, createTime, amount);

        this.dealerId = dealerId;
        this.servicePeriod = servicePeriod;
    }

    public DealerId dealerId() {
        return dealerId;
    }

    public LocalDateTime serviceExpiryTime() {
        return servicePeriod.getEnd();
    }

    @Override
    public OrderType orderType() {
        return OrderType.DEALER_SERVICE_PURCHASE_ORDER;
    }

    public DealerServicePurchaseOrderSnapshot snapshot() {
        return new DealerServicePurchaseOrderSnapshot(
                (DealerServicePurchaseOrderId) id,
                dealerId,
                servicePeriod,
                status,
                createTime,
                amount);
    }
}
