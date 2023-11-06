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

import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.InitiateRefundResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentService;
import com.yujunyang.iddd.dealer.domain.payment.RefundOrder;
import com.yujunyang.iddd.dealer.domain.payment.RefundResult;
import org.springframework.stereotype.Service;

@Service
public class WechatPaymentService implements PaymentService {
    @Override
    public boolean isMatched(PaymentChannelType paymentChannelType) {
        return PaymentChannelType.WECHAT.equals(paymentChannelType);
    }

    @Override
    public InitiatePaymentResult initiatePayment(PaymentOrder paymentOrder) {
        return null;
    }

    @Override
    public PaymentResult queryPaymentOrderStatus(PaymentOrder paymentOrder) {
        return null;
    }

    @Override
    public void closePaymentOrder(PaymentOrder paymentOrder) {

    }

    @Override
    public InitiateRefundResult initiateRefund(RefundOrder refundOrder) {
        return null;
    }

    @Override
    public RefundResult queryRefundOrderStatus(RefundOrder refundOrder) {
        return null;
    }
}