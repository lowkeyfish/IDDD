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

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderService {
    private PaymentServiceSelector paymentServiceSelector;
    private PaymentOrderRepository paymentOrderRepository;

    @Autowired
    public PaymentOrderService(
            PaymentServiceSelector paymentServiceSelector,
            PaymentOrderRepository paymentOrderRepository) {
        this.paymentServiceSelector = paymentServiceSelector;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    public void handlePaymentSuccess(PaymentOrder paymentOrder) {
        CheckUtils.notNull(paymentOrder, "paymentOrder 必须不为 null");

        PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(
                paymentOrder.paymentChannelType());

        PaymentResult paymentResult = paymentService.queryPaymentOrderStatus(paymentOrder);
        CheckUtils.isTrue(
                paymentResult.isPaymentSuccess(),
                new BusinessRuleException(
                        "支付订单状态实时查询非支付成功",
                        ImmutableMap.of(
                                "paymentOrderId",
                                paymentOrder.id().getId()
                        )
                )
        );

        paymentOrder.handlePaymentSuccess();
        paymentOrderRepository.save(paymentOrder);
    }
}
