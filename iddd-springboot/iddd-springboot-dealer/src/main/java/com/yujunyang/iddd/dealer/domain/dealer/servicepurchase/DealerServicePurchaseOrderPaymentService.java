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

import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.InitiateRefundService;
import com.yujunyang.iddd.dealer.domain.payment.PaymentServiceSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServicePurchaseOrderPaymentService {
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private PaymentServiceSelector paymentServiceSelector;
    private PaymentOrderRepository paymentOrderRepository;
    private InitiateRefundService initiateRefundService;


    @Autowired
    public DealerServicePurchaseOrderPaymentService(
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            PaymentServiceSelector paymentServiceSelector,
            PaymentOrderRepository paymentOrderRepository,
            InitiateRefundService initiateRefundService) {
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.paymentServiceSelector = paymentServiceSelector;
        this.paymentOrderRepository = paymentOrderRepository;
        this.initiateRefundService = initiateRefundService;
    }


}
