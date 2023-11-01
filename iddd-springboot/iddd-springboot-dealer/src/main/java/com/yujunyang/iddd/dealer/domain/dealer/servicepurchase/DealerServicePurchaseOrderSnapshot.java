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

import com.yujunyang.iddd.dealer.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;

public class DealerServicePurchaseOrderSnapshot {
    private DealerServicePurchaseOrderId id;
    private DealerId dealerId;
    private TimeRange servicePeriod;
    private DealerServicePurchaseOrderStatusType status;
    private LocalDateTime createTime;
    private int amount;

    public DealerServicePurchaseOrderSnapshot(
            DealerServicePurchaseOrderId id,
            DealerId dealerId,
            TimeRange servicePeriod,
            DealerServicePurchaseOrderStatusType status,
            LocalDateTime createTime, int amount) {
        this.id = id;
        this.dealerId = dealerId;
        this.servicePeriod = servicePeriod;
        this.status = status;
        this.createTime = createTime;
        this.amount = amount;
    }

    public DealerServicePurchaseOrderId getId() {
        return id;
    }

    public DealerId getDealerId() {
        return dealerId;
    }

    public TimeRange getServicePeriod() {
        return servicePeriod;
    }

    public DealerServicePurchaseOrderStatusType getStatus() {
        return status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public int getAmount() {
        return amount;
    }
}
