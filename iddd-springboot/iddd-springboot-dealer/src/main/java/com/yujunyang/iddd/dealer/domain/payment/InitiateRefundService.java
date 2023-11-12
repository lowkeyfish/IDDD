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

import com.yujunyang.iddd.common.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitiateRefundService {
    private PaymentServiceSelector paymentServiceSelector;
    private RefundOrderRepository refundOrderRepository;

    @Autowired
    public InitiateRefundService(
            PaymentServiceSelector paymentServiceSelector,
            RefundOrderRepository refundOrderRepository) {
        this.paymentServiceSelector = paymentServiceSelector;
        this.refundOrderRepository = refundOrderRepository;
    }

    public void initiateRefund(
            PaymentOrder paymentOrder,
            RefundReasonType refundReasonType) {
        CheckUtils.notNull(paymentOrder, "paymentOrder 必须不为 null");
        CheckUtils.notNull(refundReasonType, "refundReasonType 必须不为 null");

        RefundOrder refundOrder = refundOrderRepository.findByPaymentOrderId(paymentOrder.id());
        if (refundOrder != null) {
            return;
        }

        refundOrder = paymentOrder.generateRefundOrder(
                refundOrderRepository.nextId(),
                refundReasonType
        );
        refundOrder.initiateRefund(paymentServiceSelector.findPaymentServiceByChannelType(
                paymentOrder.paymentChannelType()));

        refundOrderRepository.save(refundOrder);
    }
}
