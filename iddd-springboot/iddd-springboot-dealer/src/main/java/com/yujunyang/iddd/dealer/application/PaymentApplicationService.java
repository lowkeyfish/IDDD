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
import com.yujunyang.iddd.dealer.application.command.HandleWechatPaymentNotificationCommand;
import com.yujunyang.iddd.dealer.application.command.InitiateRefundCommand;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.InitiateRefundService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentServiceSelector;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrder;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrderRepository;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentApplicationService {
    private PaymentOrderRepository paymentOrderRepository;
    private InitiateRefundService initiateRefundService;
    private RefundOrderRepository refundOrderRepository;
    private PaymentServiceSelector paymentServiceSelector;
    private RedissonClient redissonClient;


    @Autowired
    public PaymentApplicationService(
            PaymentOrderRepository paymentOrderRepository,
            InitiateRefundService initiateRefundService,
            RefundOrderRepository refundOrderRepository,
            PaymentServiceSelector paymentServiceSelector,
            RedissonClient redissonClient) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.initiateRefundService = initiateRefundService;
        this.refundOrderRepository = refundOrderRepository;
        this.paymentServiceSelector = paymentServiceSelector;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public void handleWechatPaymentNotification(
            HandleWechatPaymentNotificationCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        // 解密通知信息获取到 outTradeNo
        String outTradeNo = "";
        PaymentOrder paymentOrder = existingPaymentOrder(outTradeNo);

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "PaymentOrderId({0})",
                        paymentOrder.id()
                ),
                () -> {
                    PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(
                            paymentOrder.paymentChannelType());
                    paymentOrder.syncPaymentResult(paymentService);

                    paymentOrderRepository.save(paymentOrder);
                }
        );
    }


    @Transactional
    public void initiateRefund(InitiateRefundCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "PaymentOrderId({0})",
                        command.getPaymentOrderId()
                ),
                () -> {
                    PaymentOrder paymentOrder = existingPaymentOrder(command.getPaymentOrderId());
                    initiateRefundService.initiateRefund(paymentOrder, command.getRefundReasonType());
                }
        );
    }

    @Transactional
    public void handleWechatRefundNotification(
            HandleWechatPaymentNotificationCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        // 解密通知信息获取到 outRefundNo
        String outRefundNo = "";
        RefundOrder refundOrder = existingRefundOrder(outRefundNo);

        RedissonUtils.lock(
                redissonClient,
                MessageFormat.format(
                        "RefundOrderId({0})",
                        refundOrder.id()
                ),
                () -> {
                    PaymentService paymentService = paymentServiceSelector.findPaymentServiceByChannelType(
                            refundOrder.paymentChannelType()
                    );
                    refundOrder.syncRefundResult(paymentService);

                    refundOrderRepository.save(refundOrder);
                }
        );
    }

    private PaymentOrder existingPaymentOrder(PaymentOrderId paymentOrderId) {
        PaymentOrder paymentOrder = paymentOrderRepository.findById(paymentOrderId);
        CheckUtils.notNull(
                paymentOrder,
                new BusinessRuleException(
                        "支付单不存在",
                        ImmutableMap.of(
                                "paymentOrderId",
                                paymentOrderId.getId()
                        )
                )
        );
        return paymentOrder;
    }

    private PaymentOrder existingPaymentOrder(String outTradeNo) {
        PaymentOrder paymentOrder = paymentOrderRepository.findByOutTradeNo(outTradeNo);
        CheckUtils.notNull(
                paymentOrder,
                new BusinessRuleException(
                        "支付单不存在",
                        ImmutableMap.of(
                                "outTradeNo",
                                outTradeNo
                        )
                )
        );
        return paymentOrder;
    }

    private RefundOrder existingRefundOrder(String outRefundNo) {
        RefundOrder refundOrder = refundOrderRepository.findByOutRefundNo(outRefundNo);
        CheckUtils.notNull(
                refundOrder,
                new BusinessRuleException(
                        "退款单不存在",
                        ImmutableMap.of(
                                "outRefundNo",
                                outRefundNo
                        )
                )
        );
        return refundOrder;
    }
}
