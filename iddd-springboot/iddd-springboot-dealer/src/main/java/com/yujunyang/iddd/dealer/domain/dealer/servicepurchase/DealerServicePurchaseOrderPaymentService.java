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

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentServiceSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServicePurchaseOrderPaymentService {
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private PaymentServiceSelector paymentServiceSelector;
    private PaymentOrderRepository paymentOrderRepository;
    private PaymentOrderService paymentOrderService;

    @Autowired
    public DealerServicePurchaseOrderPaymentService(
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            PaymentServiceSelector paymentServiceSelector,
            PaymentOrderRepository paymentOrderRepository,
            PaymentOrderService paymentOrderService) {
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.paymentServiceSelector = paymentServiceSelector;
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentOrderService = paymentOrderService;
    }

    public InitiatePaymentResult initiatePayment(
            DealerServicePurchaseOrder order,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            Map<String, Object> paymentChannelParams) {
        CheckUtils.notNull(order, "order 必须不为 null");
        CheckUtils.notNull(paymentChannelType, "paymentChannelType 必须不为 null");
        CheckUtils.notNull(paymentMethodType, "paymentMethodType 必须不为 null");

        CheckUtils.isTrue(
                paymentOrderService.existsSuccessfulPaymentOrder(
                        PaymentScenarioType.DEALER_SERVICE_PURCHASE,
                        order.id()
                ),
                new BusinessRuleException(
                        "存在支付成功的支付订单,订单不能再发起支付",
                        ImmutableMap.of(
                                "id",
                                order.id().getId()
                        )
                )
        );

        PaymentOrder paymentOrder = new PaymentOrder(
                paymentOrderRepository.nextId(),
                PaymentScenarioType.DEALER_SERVICE_PURCHASE,
                order.id(),
                paymentChannelType,
                paymentMethodType,
                "服务购买",
                order.amount(),
                paymentChannelParams
        );
        PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(paymentChannelType);
        InitiatePaymentResult initiatePaymentResult = paymentOrder.initiatePayment(paymentService);
        paymentOrderRepository.save(paymentOrder);

        return initiatePaymentResult;
    }

}
