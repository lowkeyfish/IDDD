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

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceSelector {
    private List<PaymentService> paymentServices;

    @Autowired
    public PaymentServiceSelector(List<PaymentService> paymentServices) {
        this.paymentServices = paymentServices;
    }

    public PaymentService findPaymentServiceByChannelType(PaymentChannelType paymentChannelType) {
        PaymentService paymentService = paymentServices.stream()
                .filter(n -> n.isMatched(paymentChannelType)).findFirst().orElse(null);
        CheckUtils.notNull(
                paymentService,
                new BusinessRuleException(
                        "支付渠道暂不支持",
                        ImmutableMap.of(
                                "paymentChannelType",
                                paymentChannelType
                        )
                )
        );
        return paymentService;
    }
}
