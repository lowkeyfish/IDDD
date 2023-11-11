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
import org.apache.commons.collections4.CollectionUtils;
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

        // 订单状态：未发起支付、已发起支付，允许发起支付
        // 订单状态为支付失败：
        //     支付失败即使可以重新支付，也需要订单先处理失败重新发起支付，
        //     即将订单状态重置为未发起支付，同时将支付失败的订单处理关闭订单，
        //     然后再进入到订单发起支付流程时订单状态就符合未发起支付，
        //     支付失败场景的处理是单独的流程
        CheckUtils.isTrue(
                Arrays.asList(
                        OrderStatusType.PAYMENT_NOT_INITIATED,
                        OrderStatusType.PAYMENT_INITIATED
                ).contains(order.status()),
                new BusinessRuleException(
                        "订单不能发起支付,因为当前状态非未发起支付、已发起支付",
                        ImmutableMap.of(
                                "orderId",
                                order.id().getId(),
                                "orderType",
                                order.orderType(),
                                "status",
                                order.status()
                        )
                )
        );

        // 查找订单的全部支付订单
        List<PaymentOrder> allPaymentOrders = paymentOrderRepository.findByScenario(
                order.orderType(),
                order.id()
        );

        // 允许发起支付的支付单情况：
        //     1. 不存在支付订单
        //     2. 存在的支付单都是未发起支付、已发起支付、已关闭
        boolean canInitiatePayment = CollectionUtils.isEmpty(allPaymentOrders)
                || allPaymentOrders.stream().allMatch(n ->
                        Arrays.asList(
                                PaymentStatusType.NOT_INITIATED,
                                PaymentStatusType.INITIATED,
                                PaymentStatusType.CLOSED
                        ).contains(n.status())
                );

        CheckUtils.isTrue(
                canInitiatePayment,
                new BusinessRuleException(
                        "订单不能发起支付,因为订单存在未发起支付、已发起支付、支付失败三种状态外的支付单",
                        ImmutableMap.of(
                                "orderId",
                                order.id().getId(),
                                "orderType",
                                order.orderType()
                        )
                )
        );

        // 所有已发起支付的支付单
        List<PaymentOrder> initiatedPaymentOrders = allPaymentOrders.stream()
                .filter(n -> Arrays.asList(
                                PaymentStatusType.INITIATED
                        ).equals(n.status)
                ).collect(Collectors.toList());

        initiatedPaymentOrders.forEach(n -> {
            PaymentService paymentService = paymentService(n);
            PaymentResult paymentResult = paymentService.queryPaymentStatus(n);
            CheckUtils.isTrue(
                    PaymentStatusType.INITIATED.equals(paymentResult.status()),
                    new BusinessRuleException(
                            "订单不能发起支付,因为支付单实时查询状态非已发起支付",
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

        PaymentService paymentService = paymentService(paymentOrder);
        InitiatePaymentResult initiatePaymentResult = paymentOrder.initiatePayment(paymentService);
        paymentOrderRepository.save(paymentOrder);

        return initiatePaymentResult;
    }

    public PaymentService paymentService(PaymentOrder paymentOrder) {
        CheckUtils.notNull(paymentOrder, "paymentOrder 必须不为 null");

        PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(
                paymentOrder.paymentChannelType());

        return paymentService;
    }
}
