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

package com.yujunyang.iddd.dealer.domain.payment;

import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderService;

public class WechatPayPaymentStrategy implements PaymentStrategy {
    private PaymentMethodType paymentMethodType;
    private String payerOpenId;
    private WechatPayPaymentOrderService wechatPayPaymentOrderService;

    public WechatPayPaymentStrategy(
            PaymentMethodType paymentMethodType,
            String payerOpenId,
            WechatPayPaymentOrderService wechatPayPaymentOrderService) {
        this.paymentMethodType = paymentMethodType;
        this.payerOpenId = payerOpenId;
        this.wechatPayPaymentOrderService = wechatPayPaymentOrderService;
    }

    @Override
    public InitiatePaymentResult initiatePayment(
            PaymentScenarioType paymentScenarioType,
            AbstractLongId scenarioRelationId,
            int amount,
            String description) {
        return wechatPayPaymentOrderService.initiatePayment(
                paymentMethodType,
                paymentScenarioType,
                scenarioRelationId,
                amount,
                description,
                payerOpenId
        );
    }
}
