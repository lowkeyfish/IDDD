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

package com.yujunyang.iddd.dealer.domain.payment.wechatpay;

import java.time.LocalDateTime;

import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentInitiationData;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatPayPaymentOrderService {
    private WechatPayPaymentOrderRepository wechatPayPaymentOrderRepository;
    private WechatPayPaymentOrderIdGenerator wechatPayPaymentOrderIdGenerator;
    private WechatPayService wechatPayService;

    @Autowired
    public WechatPayPaymentOrderService(
            WechatPayPaymentOrderRepository wechatPayPaymentOrderRepository,
            WechatPayPaymentOrderIdGenerator wechatPayPaymentOrderIdGenerator,
            WechatPayService wechatPayService) {
        this.wechatPayPaymentOrderRepository = wechatPayPaymentOrderRepository;
        this.wechatPayPaymentOrderIdGenerator = wechatPayPaymentOrderIdGenerator;
        this.wechatPayService = wechatPayService;
    }

    public InitiatePaymentResult initiatePayment(
            PaymentMethodType paymentMethodType,
            PaymentScenarioType paymentScenarioType,
            AbstractLongId scenarioRelationId,
            int amount,
            String description,
            String payerOpenId) {
        WechatPayPaymentOrderId paymentOrderId = wechatPayPaymentOrderIdGenerator.nextId();
        WechatPayPaymentOrder paymentOrder = new WechatPayPaymentOrder(
                paymentOrderId,
                paymentScenarioType,
                scenarioRelationId,
                paymentMethodType,
                LocalDateTime.now(),
                null,
                null,
                description,
                payerOpenId.toString(),
                null,
                null,
                amount,
                payerOpenId
        );

        PaymentInitiationData paymentInitiationData = paymentOrder.initiatePayment(wechatPayService);
        wechatPayPaymentOrderRepository.save(paymentOrder);

        return new InitiatePaymentResult(
                PaymentChannelType.WECHAT_PAY,
                paymentOrderId,
                paymentInitiationData
        );
    }
}
