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

package com.yujunyang.iddd.dealer.application.command;

import java.util.HashMap;
import java.util.Map;

import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;

public class InitiatePaymentCommand {
    private AbstractLongId orderId;
    private PaymentChannelType paymentChannelType;
    private PaymentMethodType paymentMethodType;
    private Map<String, Object> paymentChannelParams;

    public InitiatePaymentCommand(
            AbstractLongId orderId,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            Map<String, Object> paymentChannelParams) {
        CheckUtils.notNull(orderId, "orderId 必须不为 null");
        CheckUtils.notNull(paymentChannelType, "paymentChannelType 必须不为 null");
        CheckUtils.notNull(paymentMethodType, "paymentMethodType 必须不为 null");

        this.orderId = orderId;
        this.paymentChannelType = paymentChannelType;
        this.paymentMethodType = paymentMethodType;
        if (paymentChannelParams == null) {
            this.paymentChannelParams = new HashMap<>();
        }
    }

    public AbstractLongId getOrderId() {
        return orderId;
    }

    public PaymentChannelType getPaymentChannelType() {
        return paymentChannelType;
    }

    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    public Map<String, Object> getPaymentChannelParams() {
        return paymentChannelParams;
    }
}
