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

package com.yujunyang.iddd.dealer.infrastructure.persistence;

import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrder;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrderId;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrderRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisRefundOrderRepository implements RefundOrderRepository {
    @Override
    public RefundOrderId nextId() {
        return null;
    }

    @Override
    public RefundOrder findById(RefundOrderId id) {
        return null;
    }

    @Override
    public void save(RefundOrder refundOrder) {

    }

    @Override
    public RefundOrder findByOutTradeNo(String outTradeNo) {
        return null;
    }

    @Override
    public RefundOrder findByPaymentOrderId(PaymentOrderId paymentOrderId) {
        return null;
    }

    @Override
    public RefundOrder findByOutRefundNo(String outRefundNo) {
        return null;
    }
}
