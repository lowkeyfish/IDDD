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

package com.yujunyang.iddd.dealer.infrastructure.persistence;

import com.yujunyang.iddd.common.domain.id.IdGenerator;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrder;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderIdGenerator;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisDealerServicePurchaseOrderRepository implements
        DealerServicePurchaseOrderRepository, DealerServicePurchaseOrderIdGenerator {
    private IdGenerator idGenerator;

    @Autowired
    public MyBatisDealerServicePurchaseOrderRepository(
            IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public DealerServicePurchaseOrderId nextId() {
        return new DealerServicePurchaseOrderId(idGenerator.nextId());
    }

    @Override
    public DealerServicePurchaseOrder findById(DealerServicePurchaseOrderId id) {
        return null;
    }

    @Override
    public void save(DealerServicePurchaseOrder dealerServicePurchaseOrder) {

    }

    @Override
    public boolean existsInProcessing(DealerId dealerId) {
        return false;
    }
}
