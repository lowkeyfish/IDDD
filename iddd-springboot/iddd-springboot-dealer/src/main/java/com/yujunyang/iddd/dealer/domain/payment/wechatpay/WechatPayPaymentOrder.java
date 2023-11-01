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

package com.yujunyang.iddd.dealer.domain.payment.wechatpay;

import java.time.LocalDateTime;

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.id.AbstractLongId;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStarted;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStatusType;

public class WechatPayPaymentOrder {
    private WechatPayPaymentOrderId id;
    private PaymentScenarioType scenario;
    private AbstractLongId scenarioRelationId;
    private PaymentMethodType method;
    private LocalDateTime createTime;
    private String appId;
    private String mchId;
    private String description;
    private String outTradeNo;
    private String timeExpire;
    private String notifyUrl;
    private int amount;
    private String payerOpenId;
    private String tradeType;
    private String transactionId;
    private String tradeState;
    private String tradeStateDesc;
    private LocalDateTime notifyTime;
    private PaymentStatusType status;

    public WechatPayPaymentOrder(
            WechatPayPaymentOrderId id,
            PaymentScenarioType scenario,
            AbstractLongId scenarioRelationId,
            PaymentMethodType method,
            LocalDateTime createTime,
            String appId,
            String mchId,
            String description,
            String outTradeNo,
            String timeExpire,
            String notifyUrl,
            int amount,
            String payerOpenId,
            String tradeType,
            String transactionId,
            String tradeState,
            String tradeStateDesc,
            LocalDateTime notifyTime,
            PaymentStatusType status) {
        this.id = id;
        this.scenario = scenario;
        this.scenarioRelationId = scenarioRelationId;
        this.method = method;
        this.createTime = createTime;
        this.appId = appId;
        this.mchId = mchId;
        this.description = description;
        this.outTradeNo = outTradeNo;
        this.timeExpire = timeExpire;
        this.notifyUrl = notifyUrl;
        this.amount = amount;
        this.payerOpenId = payerOpenId;
        this.tradeType = tradeType;
        this.transactionId = transactionId;
        this.tradeState = tradeState;
        this.tradeStateDesc = tradeStateDesc;
        this.notifyTime = notifyTime;
        this.status = status;
    }

    public String createTransaction(WechatPayService wechatPayService) {
        String transaction = wechatPayService.createTransaction(this);
        DomainEventPublisher.instance().publish(new PaymentStarted(
                DateTimeUtilsEnhance.epochMilliSecond(),
                PaymentChannelType.WECHAT_PAY,
                id.getId()
        ));
        return transaction;
    }
}
