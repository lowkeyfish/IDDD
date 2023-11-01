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

package com.yujunyang.iddd.dealer.domain.dealer.servicepurchase;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentScenarioType;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderIdGenerator;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServicePurchaseOrderPaymentService {
    private DealerRepository dealerRepository;
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private DealerServicePurchaseOrderService dealerServicePurchaseOrderService;
    private DealerServicePurchaseOrderIdGenerator dealerServicePurchaseOrderIdGenerator;
    private WechatPayPaymentOrderIdGenerator wechatPayPaymentOrderIdGenerator;
    private WechatPayService wechatPayService;


    @Autowired
    public DealerServicePurchaseOrderPaymentService(
            DealerRepository dealerRepository,
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            DealerServicePurchaseOrderService dealerServicePurchaseOrderService,
            DealerServicePurchaseOrderIdGenerator dealerServicePurchaseOrderIdGenerator,
            WechatPayPaymentOrderIdGenerator wechatPayPaymentOrderIdGenerator,
            WechatPayService wechatPayService) {
        this.dealerRepository = dealerRepository;
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.dealerServicePurchaseOrderService = dealerServicePurchaseOrderService;
        this.dealerServicePurchaseOrderIdGenerator = dealerServicePurchaseOrderIdGenerator;
        this.wechatPayPaymentOrderIdGenerator = wechatPayPaymentOrderIdGenerator;
        this.wechatPayService = wechatPayService;
    }

    public void payPurchaseServiceOrder(
            DealerServicePurchaseOrder order,
            PaymentChannelType paymentChannel,
            PaymentMethodType paymentMethod) {


    }

    public void refundPurchaseServiceOrder(
            DealerServicePurchaseOrderId orderId) {

    }
}
