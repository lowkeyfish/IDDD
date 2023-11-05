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

package com.yujunyang.iddd.dealer.domain.dealer.servicepurchase;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStrategy;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServicePurchaseOrderPaymentService {
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private WechatPayPaymentOrderService wechatPayPaymentOrderService;


    @Autowired
    public DealerServicePurchaseOrderPaymentService(
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            WechatPayPaymentOrderService wechatPayPaymentOrderService) {
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.wechatPayPaymentOrderService = wechatPayPaymentOrderService;
    }

    public InitiatePaymentResult initiatePayment(
            DealerServicePurchaseOrder order,
            PaymentStrategy paymentStrategy) {
        CheckUtils.notNull(order, "order 必须不为 null");

        if (order.isPaymentNotInitiated()) {
            return initiatePaymentForPaymentNotInitiated(order, paymentStrategy);
        }

        PaymentChannelType paymentChannelType = order.paymentChannelType();
        if (PaymentChannelType.WECHAT_PAY.equals(paymentChannelType)) {
            wechatPayPaymentOrderService.close((WechatPayPaymentOrderId) order.paymentOrderId());
        }

        return initiatePaymentForPaymentNotInitiated(order, paymentStrategy);
    }

    private InitiatePaymentResult initiatePaymentForPaymentNotInitiated(
            DealerServicePurchaseOrder order,
            PaymentStrategy paymentStrategy) {
        InitiatePaymentResult initiatePaymentResult = paymentStrategy.initiatePayment(
                PaymentScenarioType.DEALER_SERVICE_PURCHASE,
                order.id(),
                order.amount(),
                "服务购买"
        );
        order.initiatePayment(
                initiatePaymentResult.getPaymentChannelType(),
                initiatePaymentResult.getPaymentOrderId()
        );
        dealerServicePurchaseOrderRepository.save(order);

        return initiatePaymentResult;
    }
}
