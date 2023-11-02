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

package com.yujunyang.iddd.dealer.domain.payment.wechatpay;

import java.time.LocalDateTime;
import javax.annotation.Nullable;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStatusType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatPayPaymentOrderFactory {
    private WechatPayPaymentOrderIdGenerator wechatPayPaymentOrderIdGenerator;

    @Autowired
    public WechatPayPaymentOrderFactory(
            WechatPayPaymentOrderIdGenerator wechatPayPaymentOrderIdGenerator) {
        this.wechatPayPaymentOrderIdGenerator = wechatPayPaymentOrderIdGenerator;
    }

    public WechatPayPaymentOrder createPaymentOrder(
            DealerServicePurchaseOrder dealerServicePurchaseOrder,
            PaymentMethodType method,
            @Nullable String payerOpenId) {
        CheckUtils.notNull(dealerServicePurchaseOrder, "dealerServicePurchaseOrder 必须不为 null");
        CheckUtils.notNull(method, "method 必须不为 null");
        CheckUtils.isTrue(
                !PaymentMethodType.WECHAT_PAY_JSAPI.equals(method) || StringUtils.isNotBlank(payerOpenId),
                "payerOpenId 必须不为空"
        );

        WechatPayPaymentOrderId paymentOrderId = wechatPayPaymentOrderIdGenerator.nextId();

        WechatPayPaymentOrder paymentOrder = new WechatPayPaymentOrder(
                paymentOrderId,
                PaymentScenarioType.DEALER_SERVICE_PURCHASE,
                dealerServicePurchaseOrder.id(),
                method,
                LocalDateTime.now(),
                null,
                null,
                "服务购买",
                paymentOrderId.toString(),
                null,
                null,
                dealerServicePurchaseOrder.snapshot().getAmount(),
                payerOpenId,
                null,
                null,
                null,
                null,
                null,
                PaymentStatusType.NOT_INITIATED
        );

        return paymentOrder;
    }
}
