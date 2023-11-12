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
import com.yujunyang.iddd.common.utils.RedissonUtils;
import com.yujunyang.iddd.dealer.application.command.InitiatePaymentCommand;
import com.yujunyang.iddd.dealer.application.command.OrderPaymentStatusChangeCommand;
import com.yujunyang.iddd.dealer.application.command.OrderRefundStatusChangeCommand;
import com.yujunyang.iddd.dealer.application.command.OrderRequestRefundCommand;
import com.yujunyang.iddd.dealer.application.command.PurchaseServiceCommand;
import com.yujunyang.iddd.dealer.application.data.InitiatePaymentCommandResult;
import com.yujunyang.iddd.dealer.application.data.PurchaseServiceCommandResult;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrder;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderFactory;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrder;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrderId;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrderRepository;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerServicePurchaseOrderApplicationService {
    private DealerRepository dealerRepository;
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private DealerServicePurchaseOrderFactory dealerServicePurchaseOrderFactory;
    private InitiatePaymentService initiatePaymentService;
    private PaymentOrderRepository paymentOrderRepository;
    private RefundOrderRepository refundOrderRepository;
    private RedissonClient redissonClient;


    @Autowired
    public DealerServicePurchaseOrderApplicationService(
            DealerRepository dealerRepository,
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            DealerServicePurchaseOrderFactory dealerServicePurchaseOrderFactory,
            InitiatePaymentService initiatePaymentService,
            PaymentOrderRepository paymentOrderRepository,
            RefundOrderRepository refundOrderRepository,
            RedissonClient redissonClient) {
        this.dealerRepository = dealerRepository;
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.dealerServicePurchaseOrderFactory = dealerServicePurchaseOrderFactory;
        this.initiatePaymentService = initiatePaymentService;
        this.paymentOrderRepository = paymentOrderRepository;
        this.refundOrderRepository = refundOrderRepository;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public void purchaseService(
            PurchaseServiceCommand command,
            PurchaseServiceCommandResult commandResult) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerId({0})",
                        command.getDealerId()
                ),
                () -> {
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
        );
    }

    @Transactional
    public void initiatePayment(
            InitiatePaymentCommand command,
            InitiatePaymentCommandResult commandResult) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerServicePurchaseOrderId({0})",
                        command.getOrderId()
                ),
                () -> {
                    DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());

                    InitiatePaymentResult initiatePaymentResult = initiatePaymentService.initiatePayment(
                            order,
                            command.getPaymentChannelType(),
                            command.getPaymentMethodType(),
                            command.getPaymentChannelParams()
                    );

                    commandResult.resultingPaymentInitiationData(initiatePaymentResult.getData());
                }
        );
    }

    @Transactional
    public void markAsPaymentInitiated(OrderPaymentStatusChangeCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerServicePurchaseOrderId({0})",
                        command.getOrderId()
                ),
                () -> {
                    DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());
                    PaymentOrder paymentOrder = existingPaymentOrder(command.getPaymentOrderId());
                    order.markAsPaymentInitiated(paymentOrder);

                    dealerServicePurchaseOrderRepository.save(order);
                }
        );
    }

    @Transactional
    public void markAsPaid(OrderPaymentStatusChangeCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerServicePurchaseOrderId({0})",
                        command.getOrderId()
                ),
                () -> {
                    DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());
                    PaymentOrder paymentOrder = existingPaymentOrder(command.getPaymentOrderId());
                    order.markAsPaid(paymentOrder);

                    dealerServicePurchaseOrderRepository.save(order);
                }
        );
    }

    @Transactional
    public void markAsFailed(OrderPaymentStatusChangeCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerServicePurchaseOrderId({0})",
                        command.getOrderId()
                ),
                () -> {
                    DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());
                    PaymentOrder paymentOrder = existingPaymentOrder(command.getPaymentOrderId());
                    order.markAsPaymentFailed(paymentOrder);

                    dealerServicePurchaseOrderRepository.save(order);
                }
        );
    }

    @Transactional
    public void requestRefund(OrderRequestRefundCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerServicePurchaseOrderId({0})",
                        command.getOrderId()
                ),
                () -> {
                    DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());
                    order.requestRefund();

                    dealerServicePurchaseOrderRepository.save(order);
                }
        );
    }

    @Transactional
    public void markAsRefundInitiated(OrderRefundStatusChangeCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerServicePurchaseOrderId({0})",
                        command.getOrderId()
                ),
                () -> {
                    DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());
                    RefundOrder refundOrder = existingRefundOrder(command.getRefundOrderId());
                    order.markAsRefundInitiated(refundOrder);

                    dealerServicePurchaseOrderRepository.save(order);
                }
        );
    }

    @Transactional
    public void markAsRefunded(OrderRefundStatusChangeCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "DealerServicePurchaseOrderId({0})",
                        command.getOrderId()
                ),
                () -> {
                    DealerServicePurchaseOrder order = existingOrder((DealerServicePurchaseOrderId) command.getOrderId());
                    RefundOrder refundOrder = existingRefundOrder(command.getRefundOrderId());
                    order.markAsRefunded(refundOrder);

                    dealerServicePurchaseOrderRepository.save(order);
                }
        );
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
        CheckUtils.notNull(
                dealer,
                "Dealer({0})不存在",
                dealerId
        );
        return dealer;
    }

    private PaymentOrder existingPaymentOrder(PaymentOrderId paymentOrderId) {
        PaymentOrder paymentOrder = paymentOrderRepository.findById(paymentOrderId);
        CheckUtils.notNull(
                paymentOrder,
                "PaymentOrder({0})不存在",
                paymentOrderId
        );
        return paymentOrder;
    }

    private RefundOrder existingRefundOrder(RefundOrderId refundOrderId) {
        RefundOrder paymentOrder = refundOrderRepository.findById(refundOrderId);
        CheckUtils.notNull(
                paymentOrder,
                "RefundOrder({0})不存在",
                refundOrderId
        );
        return paymentOrder;
    }
}
