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

import com.yujunyang.iddd.common.domain.id.AbstractLongId;

public class InitiatePaymentResult {
    private PaymentChannelType paymentChannelType;
    private AbstractLongId paymentOrderId;
    private PaymentInitiationData paymentInitiationData;

    public InitiatePaymentResult(
            PaymentChannelType paymentChannelType,
            AbstractLongId paymentOrderId,
            PaymentInitiationData paymentInitiationData) {
        this.paymentChannelType = paymentChannelType;
        this.paymentOrderId = paymentOrderId;
        this.paymentInitiationData = paymentInitiationData;
    }

    public PaymentChannelType getPaymentChannelType() {
        return paymentChannelType;
    }

    public AbstractLongId getPaymentOrderId() {
        return paymentOrderId;
    }

    public PaymentInitiationData getPaymentInitiationData() {
        return paymentInitiationData;
    }
}
