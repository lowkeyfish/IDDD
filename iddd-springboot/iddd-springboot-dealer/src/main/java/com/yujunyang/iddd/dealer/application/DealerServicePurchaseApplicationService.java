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
import com.yujunyang.iddd.dealer.application.command.DealerPurchaseServiceOrderInitiateWechatPayPaymentCommand;
import com.yujunyang.iddd.dealer.application.command.PurchaseServiceCommand;
import com.yujunyang.iddd.dealer.application.data.InitiatePaymentCommand;
import com.yujunyang.iddd.dealer.application.data.PurchaseServiceCommandResult;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrder;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderFactory;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.WechatPayPaymentStrategy;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderService;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderService;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerServicePurchaseApplicationService {
    private DealerRepository dealerRepository;
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private DealerServicePurchaseOrderFactory dealerServicePurchaseOrderFactory;
    private PaymentOrderService paymentOrderService;
    private WechatPayPaymentOrderRepository wechatPayPaymentOrderRepository;
    private WechatPayService wechatPayService;
    private WechatPayPaymentOrderService wechatPayPaymentOrderService;

    @Autowired
    public DealerServicePurchaseApplicationService(
            DealerRepository dealerRepository,
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            DealerServicePurchaseOrderFactory dealerServicePurchaseOrderFactory,
            PaymentOrderService paymentOrderService,
            WechatPayPaymentOrderRepository wechatPayPaymentOrderRepository,
            WechatPayService wechatPayService,
            WechatPayPaymentOrderService wechatPayPaymentOrderService) {
        this.dealerRepository = dealerRepository;
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.dealerServicePurchaseOrderFactory = dealerServicePurchaseOrderFactory;
        this.paymentOrderService = paymentOrderService;
        this.wechatPayPaymentOrderRepository = wechatPayPaymentOrderRepository;
        this.wechatPayService = wechatPayService;
        this.wechatPayPaymentOrderService = wechatPayPaymentOrderService;
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
    public void initiateWechatPayPayment(
            DealerPurchaseServiceOrderInitiateWechatPayPaymentCommand command,
            InitiatePaymentCommand commandResult) {
        CheckUtils.notNull(command, "command 必须不为 null");

        DealerServicePurchaseOrder order = existingOrder(command.getDealerServicePurchaseOrderId());

        String paymentInitiationData = order.initiatePayment(
                new WechatPayPaymentStrategy(
                        command.getPaymentMethod(),
                        command.getWechatOpenId(),
                        wechatPayPaymentOrderService
                )
        );

        dealerServicePurchaseOrderRepository.save(order);

        commandResult.resultingPaymentInitiationData(paymentInitiationData);
    }

    @Transactional
    public void notifyPurchaseServiceOrderPayment() {

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
