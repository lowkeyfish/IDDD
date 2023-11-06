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

package com.yujunyang.iddd.dealer.application;

import java.text.MessageFormat;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.application.command.DealerServicePurchaseOrderInitiateWechatPayPaymentCommand;
import com.yujunyang.iddd.dealer.application.command.DealerServicePurchaseOrderHandlePaymentSuccessCommand;
import com.yujunyang.iddd.dealer.application.command.InitiatePaymentCommand;
import com.yujunyang.iddd.dealer.application.command.PurchaseServiceCommand;
import com.yujunyang.iddd.dealer.application.data.InitiatePaymentCommandResult;
import com.yujunyang.iddd.dealer.application.data.PurchaseServiceCommandResult;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrder;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderFactory;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderPaymentService;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import com.yujunyang.iddd.dealer.domain.payment.WechatPayPaymentStrategy;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerServicePurchaseApplicationService {
    private DealerRepository dealerRepository;
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private DealerServicePurchaseOrderFactory dealerServicePurchaseOrderFactory;
    private WechatPayPaymentOrderService wechatPayPaymentOrderService;
    private DealerServicePurchaseOrderPaymentService dealerServicePurchaseOrderPaymentService;
    private PaymentOrderRepository paymentOrderRepository;

    @Autowired
    public DealerServicePurchaseApplicationService(
            DealerRepository dealerRepository,
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            DealerServicePurchaseOrderFactory dealerServicePurchaseOrderFactory,
            WechatPayPaymentOrderService wechatPayPaymentOrderService,
            DealerServicePurchaseOrderPaymentService dealerServicePurchaseOrderPaymentService,
            PaymentOrderRepository paymentOrderRepository) {
        this.dealerRepository = dealerRepository;
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.dealerServicePurchaseOrderFactory = dealerServicePurchaseOrderFactory;
        this.wechatPayPaymentOrderService = wechatPayPaymentOrderService;
        this.dealerServicePurchaseOrderPaymentService = dealerServicePurchaseOrderPaymentService;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Transactional
    public void purchaseService(
            PurchaseServiceCommand command,
            PurchaseServiceCommandResult commandResult) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());

        boolean existsInProcessing = dealerServicePurchaseOrderRepository.existsInProcessing(command.getDealerId());
        CheckUtils.isTrue(
                !existsInProcessing,
                new BusinessRuleException(
                        "存在未完成的服务购买订单",
                        ImmutableMap.of(
                                "dealerId",
                                command.getDealerId()
                        )
                )
        );

        DealerServicePurchaseOrder dealerServicePurchaseOrder =
                dealerServicePurchaseOrderFactory.createServicePurchaseOrder(dealer);
        dealerServicePurchaseOrderRepository.save(dealerServicePurchaseOrder);

        commandResult.resultingPurchaseServiceOrderId(dealerServicePurchaseOrder.id().getId());
    }

    @Transactional
    public void initiatePayment(
            InitiatePaymentCommand command,
            InitiatePaymentCommandResult commandResult) {
        DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());

        InitiatePaymentResult initiatePaymentResult = dealerServicePurchaseOrderPaymentService.initiatePayment(
                order,
                command.getPaymentChannelType(),
                command.getPaymentMethodType(),
                command.getPaymentChannelParams()
        );

        commandResult.resultingPaymentInitiationData(initiatePaymentResult.getData());
    }

    @Transactional
    public void handlePaymentSuccess(
            DealerServicePurchaseOrderHandlePaymentSuccessCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        DealerServicePurchaseOrder order = existingOrder(command.getDealerServicePurchaseOrderId());
        order.handleSuccessfulPayment();

        dealerServicePurchaseOrderRepository.save(order);
    }

    @Transactional
    public void refundPurchaseServiceOrder() {

    }

    @Transactional
    public void notifyPurchaseServiceOrderRefund() {

    }

    private DealerServicePurchaseOrder existingOrder(DealerServicePurchaseOrderId id) {
        DealerServicePurchaseOrder order = dealerServicePurchaseOrderRepository.findById(id);

        CheckUtils.notNull(
                order,
                "DealerServicePurchaseOrderId({0})无效",
                id
        );

        return order;
    }

    private Dealer existingDealer(DealerId dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId);
        if (dealer == null) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Dealer({0}) 不存在", dealerId)
            );
        }
        return dealer;
    }
}
