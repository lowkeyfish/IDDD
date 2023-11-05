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
 *
 */

package com.yujunyang.iddd.dealer.domain.dealer.servicepurchase;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServicePurchaseOrderFactory {
    private DealerServicePurchaseOrderIdGenerator dealerServicePurchaseOrderIdGenerator;
    private DealerServicePurchaseAmountService dealerServicePurchaseAmountService;

    @Autowired
    public DealerServicePurchaseOrderFactory(
            DealerServicePurchaseOrderIdGenerator dealerServicePurchaseOrderIdGenerator,
            DealerServicePurchaseAmountService dealerServicePurchaseAmountService) {
        this.dealerServicePurchaseOrderIdGenerator = dealerServicePurchaseOrderIdGenerator;
        this.dealerServicePurchaseAmountService = dealerServicePurchaseAmountService;
    }

    public DealerServicePurchaseOrder createServicePurchaseOrder(
            Dealer dealer) {
        CheckUtils.notNull(dealer, "dealer 必须不为 null");

        TimeRange servicePeriod = dealer.nextServicePeriod();

        return new DealerServicePurchaseOrder(
                dealerServicePurchaseOrderIdGenerator.nextId(),
                dealer.id(),
                servicePeriod,
                dealerServicePurchaseAmountService.calculateAmount(dealer, servicePeriod)
        );
    }
}
