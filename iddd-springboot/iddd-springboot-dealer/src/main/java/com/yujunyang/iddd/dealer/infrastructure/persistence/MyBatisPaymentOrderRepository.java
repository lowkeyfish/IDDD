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

import java.util.ArrayList;
import java.util.List;

import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.order.OrderType;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisPaymentOrderRepository implements PaymentOrderRepository {
    @Override
    public PaymentOrderId nextId() {
        return null;
    }

    @Override
    public void save(PaymentOrder paymentOrder) {

    }

    @Override
    public PaymentOrder findById(PaymentOrderId id) {
        return null;
    }

    @Override
    public PaymentOrder findByOutTradeNo(String outTradeNo) {
        return null;
    }

    @Override
    public List<PaymentOrder> findByScenario(
            OrderType paymentScenarioType,
            AbstractLongId scenarioRelationId) {
        return new ArrayList<>();
    }
}
