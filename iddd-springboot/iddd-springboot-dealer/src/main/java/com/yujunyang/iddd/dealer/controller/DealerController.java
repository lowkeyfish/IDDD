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

package com.yujunyang.iddd.dealer.controller;

import java.util.Arrays;

import com.yujunyang.iddd.common.data.RestResponse;
import com.yujunyang.iddd.common.exception.NameNotUniqueException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.application.DealerApplicationService;
import com.yujunyang.iddd.dealer.application.DealerQueryService;
import com.yujunyang.iddd.dealer.application.DealerServicePurchaseApplicationService;
import com.yujunyang.iddd.dealer.application.command.DealerChangeNameCommand;
import com.yujunyang.iddd.dealer.application.command.DealerCreateCommand;
import com.yujunyang.iddd.dealer.application.command.PurchaseServiceCommand;
import com.yujunyang.iddd.dealer.application.command.DealerServicePurchaseOrderInitiateWechatPayPaymentCommand;
import com.yujunyang.iddd.dealer.application.data.DealerViewModel;
import com.yujunyang.iddd.dealer.controller.input.DealerRequestBody;
import com.yujunyang.iddd.dealer.controller.input.InitiatePaymentRequestBody;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class DealerController {
    private DealerApplicationService dealerApplicationService;
    private DealerQueryService dealerQueryService;
    private DealerServicePurchaseApplicationService dealerServicePurchaseApplicationService;

    @Autowired
    public DealerController(
            DealerApplicationService dealerApplicationService,
            DealerQueryService dealerQueryService,
            DealerServicePurchaseApplicationService dealerServicePurchaseApplicationService) {
        this.dealerApplicationService = dealerApplicationService;
        this.dealerQueryService = dealerQueryService;
        this.dealerServicePurchaseApplicationService = dealerServicePurchaseApplicationService;
    }

    @PostMapping("/dealers")
    public RestResponse<Long> createDealer(
            @RequestBody DealerRequestBody requestBody) {
        RestResponse<Long> response = new RestResponse<>();

        try {
            dealerApplicationService.create(
                    new DealerCreateCommand(
                            requestBody.getName(),
                            requestBody.getCityId(),
                            requestBody.getSpecificAddress(),
                            requestBody.getTelephone(),
                            requestBody.getBrandId()
                    ),
                    dealerId -> response.setData(dealerId)
            );
        } catch (NameNotUniqueException e) {
            return new RestResponse<>(400101, e.getMessage());
        }

        return response;
    }

    @GetMapping("/dealers/{dealerId}")
    public RestResponse<DealerViewModel> findDealerById(
            @PathVariable("dealerId") long dealerId) {
        DealerViewModel dealer = dealerQueryService.findDealerById(new DealerId(dealerId));
        return new RestResponse<>(dealer);
    }

    @PutMapping("/dealers/{dealerId}/name")
    public RestResponse changeDealerName(
            @PathVariable("dealerId") long dealerId,
            @RequestBody DealerRequestBody requestBody) {
        dealerApplicationService.changeName(new DealerChangeNameCommand(
                new DealerId(dealerId),
                requestBody.getName()
        ));
        return new RestResponse();
    }

    @PostMapping("/dealers/{dealerId}/service-purchase-orders")
    public RestResponse<Long> purchaseService(
            @PathVariable("dealerId") long dealerId) {
        RestResponse<Long> response = new RestResponse<>();
        dealerServicePurchaseApplicationService.purchaseService(
                new PurchaseServiceCommand(
                        new DealerId(dealerId)
                ), id -> response.setData(id)
        );
        return response;
    }

    @PostMapping("/dealer-service-purchase-orders/{orderId}/payments")
    public RestResponse initiatePayment(
            @PathVariable("orderId") long orderId,
            @RequestBody InitiatePaymentRequestBody requestBody) {
        CheckUtils.isTrue(
                Arrays.asList(
                        PaymentChannelType.WECHAT
                ).contains(requestBody.getPaymentChannel()),
                "paymentChannel({0})暂不支持",
                requestBody.getPaymentChannel()
        );

        RestResponse<String> response = new RestResponse<>();
        if (PaymentChannelType.WECHAT.equals(requestBody.getPaymentChannel())) {
            dealerServicePurchaseApplicationService.initiateWechatPayPayment(
                    new DealerServicePurchaseOrderInitiateWechatPayPaymentCommand(
                            new DealerServicePurchaseOrderId(orderId),
                            requestBody.getPaymentMethod(),
                            requestBody.getWechatOpenId()
                    ),
                    paymentInitiationData -> response.setData(paymentInitiationData)
            );
        }

        return response;
    }
}
