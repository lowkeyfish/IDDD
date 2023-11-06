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

import com.yujunyang.iddd.dealer.domain.payment.PaymentInitiationResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderDetails;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayRefundInitiationData;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayRefundOrder;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayRefundOrderDetails;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayService;
import org.springframework.stereotype.Service;

@Service
public class WechatPayServiceImpl implements WechatPayService {
    @Override
    public PaymentInitiationResult initiatePayment(
            PaymentMethodType paymentMethodType,
            String outTradeNo,
            String description,
            int amount,
            String timeExpire) {
        return null;
    }

    @Override
    public PaymentInitiationResult initiatePayment(PaymentOrder paymentOrder) {
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
