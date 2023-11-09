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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.order.AbstractOrder;
import com.yujunyang.iddd.dealer.domain.order.OrderStatusType;
import com.yujunyang.iddd.dealer.domain.order.OrderType;
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

    public InitiatePaymentResult initiatePayment(
            AbstractOrder order,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            Map<String, Object> paymentChannelParams) {
        CheckUtils.notNull(order, "order 必须不为 null");
        CheckUtils.notNull(paymentChannelType, "paymentChannelType 必须不为 null");
        CheckUtils.notNull(paymentMethodType, "paymentMethodType 必须不为 null");

        // 订单状态：未支付、支付失败，允许发起支付
        CheckUtils.isTrue(
                Arrays.asList(
                        OrderStatusType.PAYMENT_NOT_INITIATED,
                        OrderStatusType.PAYMENT_FAILED
                ).contains(order.status()),
                new BusinessRuleException(
                        "订单当前状态非未支付或支付失败,不能再发起支付",
                        ImmutableMap.of(
                                "orderId",
                                order.id().getId(),
                                "orderType",
                                order.orderType(),
                                "orderStatus",
                                order.status()
                        )
                )
        );

        // 查找订单的全部支付订单
        List<PaymentOrder> allPaymentOrders = paymentOrderRepository.findByScenario(
                order.orderType(),
                order.id()
        );

        // 允许发起支付的支付单情况：不存在支付订单或支付订单状态是已发起支付、支付失败
        boolean canInitiatePayment = allPaymentOrders.stream().allMatch(n -> Arrays.asList(
                PaymentStatusType.INITIATED,
                PaymentStatusType.FAILED
        ).contains(n.status()));

        CheckUtils.isTrue(
                canInitiatePayment,
                new BusinessRuleException(
                        "订单存在已支付的支付单,不能再发起支付",
                        ImmutableMap.of(
                                "orderId",
                                order.id().getId(),
                                "orderType",
                                order.orderType()
                        )
                )
        );

        // 筛选和当前发起支付渠道、支付类型一致的支付订单
        List<PaymentOrder> initiatedPaymentOrders = allPaymentOrders.stream()
                .filter(n -> n.paymentChannelType().equals(paymentChannelType)
                        && n.paymentMethodType().equals(paymentMethodType)
                        && n.status().equals(PaymentStatusType.INITIATED)
                ).collect(Collectors.toList());

        initiatedPaymentOrders.forEach(n -> {
            PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(n.paymentChannelType());
            PaymentResult paymentResult = paymentService.queryPaymentStatus(n);
            CheckUtils.isTrue(
                    paymentResult.isUnpaid(),
                    new BusinessRuleException(
                            "订单支付单状态非未支付,不能再发起支付",
                            ImmutableMap.of(
                                    "orderId",
                                    order.id().getId(),
                                    "orderType",
                                    order.orderType(),
                                    "paymentOrderId",
                                    n.id().getId()
                            )
                    )
            );
        });

        PaymentOrder paymentOrder = allPaymentOrders.stream().filter(n ->
                n.paymentChannelType().equals(paymentChannelType)
                        && n.paymentMethodType().equals(paymentMethodType)
                        && n.status().equals(PaymentStatusType.INITIATED)
        ).findFirst().orElse(null);
        if (paymentOrder == null) {
            paymentOrder = new PaymentOrder(
                    paymentOrderRepository.nextId(),
                    OrderType.DEALER_SERVICE_PURCHASE_ORDER,
                    order.id(),
                    paymentChannelType,
                    paymentMethodType,
                    MessageFormat.format(
                            "订单{0}",
                            order.id()
                    ),
                    order.amount(),
                    paymentChannelParams
            );
        }

        PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(paymentChannelType);
        InitiatePaymentResult initiatePaymentResult = paymentOrder.initiatePayment(paymentService);
        paymentOrderRepository.save(paymentOrder);

        return initiatePaymentResult;
    }

    public void handlePaymentNotification(PaymentOrder paymentOrder) {
        CheckUtils.notNull(paymentOrder, "paymentOrder 必须不为 null");

        PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(
                paymentOrder.paymentChannelType());

        PaymentResult paymentResult = paymentService.queryPaymentStatus(paymentOrder);
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

        paymentOrder.markAsPaymentSuccess();
        paymentOrderRepository.save(paymentOrder);
    }
}
