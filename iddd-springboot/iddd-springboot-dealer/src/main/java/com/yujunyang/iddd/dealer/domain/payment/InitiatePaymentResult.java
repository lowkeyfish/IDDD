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
 *
 */

package com.yujunyang.iddd.dealer.domain.payment;

public class InitiatePaymentResult {
    private PaymentOrderId paymentOrderId;
    private PaymentChannelType paymentChannelType;
    private PaymentMethodType paymentMethodType;
    private String data;

    public InitiatePaymentResult(
            PaymentOrderId paymentOrderId,
            PaymentChannelType paymentChannelType,
            PaymentMethodType paymentMethodType,
            String data) {
        this.paymentOrderId = paymentOrderId;
        this.paymentChannelType = paymentChannelType;
        this.paymentMethodType = paymentMethodType;
        this.data = data;
    }

    public PaymentOrderId getPaymentOrderId() {
        return paymentOrderId;
    }

    public PaymentChannelType getPaymentChannelType() {
        return paymentChannelType;
    }

    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    public String getData() {
        return data;
    }
}
