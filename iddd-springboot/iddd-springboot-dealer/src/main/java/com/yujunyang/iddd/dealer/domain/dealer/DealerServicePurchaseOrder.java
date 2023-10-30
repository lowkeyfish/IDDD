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

package com.yujunyang.iddd.dealer.domain.dealer;

import java.time.LocalDateTime;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.common.TimeRange;

public class DealerServicePurchaseOrder {
    private DealerServicePurchaseOrderId id;
    private DealerId dealerId;
    private TimeRange servicePeriod;
    private DealerServicePurchaseOrderStatusType status;
    private LocalDateTime createTime;

    public DealerServicePurchaseOrder(
            DealerServicePurchaseOrderId id,
            DealerId dealerId,
            TimeRange servicePeriod) {
        this(
                id,
                dealerId,
                servicePeriod,
                DealerServicePurchaseOrderStatusType.UNPAID,
                LocalDateTime.now()
        );

        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.notNull(dealerId, "dealerId 必须不为 null");
        CheckUtils.notNull(servicePeriod, "servicePeriod 必须不为 null");
    }

    public DealerServicePurchaseOrder(
            DealerServicePurchaseOrderId id,
            DealerId dealerId,
            TimeRange servicePeriod,
            DealerServicePurchaseOrderStatusType status,
            LocalDateTime createTime) {
        this.id = id;
        this.dealerId = dealerId;
        this.servicePeriod = servicePeriod;
        this.status = status;
        this.createTime = createTime;
    }

    public void pay() {

    }

    public void completePayment() {

    }

    public void cancel() {

    }

    public DealerServicePurchaseOrderId id() {
        return id;
    }

    public DealerServicePurchaseOrderSnapshot snapshot() {
        return new DealerServicePurchaseOrderSnapshot(
                id,
                dealerId,
                servicePeriod,
                status,
                createTime
        );
    }
}
