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

package com.yujunyang.iddd.dealer.domain.payment;

import java.time.LocalDateTime;

import com.yujunyang.iddd.common.domain.id.AbstractLongId;

public class PaymentOrder {
    private String outTradeNo;
    private String tradeNo;
    private PaymentChannelType channel;
    private PaymentMethodType method;
    private PaymentScenarioType scenario;
    private AbstractLongId scenarioRelationId;
    private LocalDateTime payTime;
    private int totalAmount;
    private PaymentStatusType status;

    public PaymentOrder(
            String outTradeNo,
            String tradeNo,
            PaymentChannelType channel,
            PaymentMethodType method,
            PaymentScenarioType scenario,
            AbstractLongId scenarioRelationId,
            LocalDateTime payTime,
            int totalAmount,
            PaymentStatusType status) {
        this.outTradeNo = outTradeNo;
        this.tradeNo = tradeNo;
        this.channel = channel;
        this.method = method;
        this.scenario = scenario;
        this.scenarioRelationId = scenarioRelationId;
        this.payTime = payTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }


}
