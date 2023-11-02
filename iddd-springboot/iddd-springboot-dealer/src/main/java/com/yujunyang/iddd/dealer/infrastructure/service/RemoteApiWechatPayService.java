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

package com.yujunyang.iddd.dealer.infrastructure.service;

import java.util.Arrays;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentInitiationData;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderDetails;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayRefundInitiationData;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayRefundOrder;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayRefundOrderDetails;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayService;
import org.springframework.stereotype.Service;

@Service
public class RemoteApiWechatPayService implements WechatPayService {

    @Override
    public PaymentInitiationData initiatePayment(WechatPayPaymentOrder paymentOrder) {
        CheckUtils.isTrue(
                Arrays.asList(
                        PaymentMethodType.WECHAT_PAY_JSAPI
                ).contains(paymentOrder.getPaymentMethod()),
                new BusinessRuleException("支付类型暂不支持", ImmutableMap.of(
                        "paymentChannel",
                        PaymentChannelType.WECHAT_PAY,
                        "paymentMethod",
                        paymentOrder.getPaymentMethod()
                ))
        );

        return null;
    }

    @Override
    public WechatPayPaymentOrderDetails queryPaymentOrder(String outTradeNo) {
        return null;
    }

    @Override
    public void closePaymentOrder(String outTradeNo, String mchId) {

    }

    @Override
    public WechatPayRefundInitiationData initiateRefund(WechatPayRefundOrder refundOrder) {
        return null;
    }

    @Override
    public WechatPayRefundOrderDetails queryRefundOrder(String outRefundNo) {
        return null;
    }
}
