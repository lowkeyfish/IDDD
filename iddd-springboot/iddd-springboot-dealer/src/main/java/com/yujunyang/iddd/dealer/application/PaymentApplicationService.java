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

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.application.command.HandleWechatPaymentNotificationCommand;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentApplicationService {
    private PaymentOrderRepository paymentOrderRepository;
    private PaymentOrderService paymentOrderService;


    @Autowired
    public PaymentApplicationService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentOrderService paymentOrderService) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentOrderService = paymentOrderService;
    }

    @Transactional
    public void handleWechatPaymentNotification(
            HandleWechatPaymentNotificationCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        // 解密通知信息获取到 outTradeNo
        String outTradeNo = "";
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

        PaymentService paymentService = paymentOrderService.paymentService(paymentOrder);
        paymentOrder.syncPaymentResult(paymentService);

        paymentOrderRepository.save(paymentOrder);
    }


}
