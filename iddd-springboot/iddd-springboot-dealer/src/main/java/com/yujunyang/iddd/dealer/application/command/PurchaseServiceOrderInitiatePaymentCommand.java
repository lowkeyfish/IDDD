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

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import org.apache.commons.lang3.StringUtils;

public class PurchaseServiceOrderInitiatePaymentCommand {
    private DealerServicePurchaseOrderId dealerServicePurchaseOrderId;
    private PaymentChannelType paymentChannel;
    private PaymentMethodType paymentMethod;
    private String wechatOpenId;

    public PurchaseServiceOrderInitiatePaymentCommand(
            DealerServicePurchaseOrderId dealerServicePurchaseOrderId,
            PaymentChannelType paymentChannel,
            PaymentMethodType paymentMethod,
            String wechatOpenId) {
        CheckUtils.notNull(dealerServicePurchaseOrderId, "dealerServicePurchaseOrderId 必须不为 null");
        CheckUtils.notNull(paymentChannel, "paymentChannel 必须不为 null");
        CheckUtils.notNull(paymentMethod, "paymentMethod 必须不为 null");
        CheckUtils.isTrue(
                !PaymentChannelType.WECHAT_PAY.equals(paymentChannel)
                        || !PaymentMethodType.WECHAT_PAY_JSAPI.equals(paymentMethod)
                        || StringUtils.isNotBlank(wechatOpenId),
                "wechatOpenId 必须不为空"
        );
        this.dealerServicePurchaseOrderId = dealerServicePurchaseOrderId;
        this.paymentChannel = paymentChannel;
        this.paymentMethod = paymentMethod;
        this.wechatOpenId = wechatOpenId;
    }

    public DealerServicePurchaseOrderId getDealerServicePurchaseOrderId() {
        return dealerServicePurchaseOrderId;
    }

    public PaymentChannelType getPaymentChannel() {
        return paymentChannel;
    }

    public PaymentMethodType getPaymentMethod() {
        return paymentMethod;
    }

    public String getWechatOpenId() {
        return wechatOpenId;
    }
}
