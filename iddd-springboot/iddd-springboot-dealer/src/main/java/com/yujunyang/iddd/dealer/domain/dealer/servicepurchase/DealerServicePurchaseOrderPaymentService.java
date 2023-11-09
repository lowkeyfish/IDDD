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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.order.OrderStatusType;
import com.yujunyang.iddd.dealer.domain.payment.InitiatePaymentResult;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentResult;
import com.yujunyang.iddd.dealer.domain.order.OrderType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentServiceSelector;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServicePurchaseOrderPaymentService {
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private PaymentServiceSelector paymentServiceSelector;
    private PaymentOrderRepository paymentOrderRepository;
    private PaymentOrderService paymentOrderService;


    @Autowired
    public DealerServicePurchaseOrderPaymentService(
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            PaymentServiceSelector paymentServiceSelector,
            PaymentOrderRepository paymentOrderRepository,
            PaymentOrderService paymentOrderService) {
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.paymentServiceSelector = paymentServiceSelector;
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentOrderService = paymentOrderService;
    }


}
