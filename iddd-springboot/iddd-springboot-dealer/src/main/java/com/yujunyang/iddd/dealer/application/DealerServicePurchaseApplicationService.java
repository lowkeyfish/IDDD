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

package com.yujunyang.iddd.dealer.application;

import java.text.MessageFormat;

import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.application.command.RequestPayPurchaseServiceOrderCommand;
import com.yujunyang.iddd.dealer.application.command.PurchaseServiceCommand;
import com.yujunyang.iddd.dealer.application.data.PayCommandResult;
import com.yujunyang.iddd.dealer.application.data.PurchaseServiceCommandResult;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseAmountService;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrder;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderIdGenerator;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.PaymentChannelType;
import com.yujunyang.iddd.dealer.domain.payment.PaymentMethodType;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderFactory;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayPaymentOrderRepository;
import com.yujunyang.iddd.dealer.domain.payment.wechatpay.WechatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerServicePurchaseApplicationService {
    private DealerRepository dealerRepository;
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;
    private DealerServicePurchaseOrderIdGenerator dealerServicePurchaseOrderIdGenerator;
    private DealerServicePurchaseAmountService dealerServicePurchaseAmountService;
    private WechatPayPaymentOrderFactory wechatPayPaymentOrderFactory;
    private WechatPayPaymentOrderRepository wechatPayPaymentOrderRepository;
    private WechatPayService wechatPayService;

    @Autowired
    public DealerServicePurchaseApplicationService(
            DealerRepository dealerRepository,
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository,
            DealerServicePurchaseOrderIdGenerator dealerServicePurchaseOrderIdGenerator,
            DealerServicePurchaseAmountService dealerServicePurchaseAmountService,
            WechatPayPaymentOrderFactory wechatPayPaymentOrderFactory,
            WechatPayPaymentOrderRepository wechatPayPaymentOrderRepository,
            WechatPayService wechatPayService) {
        this.dealerRepository = dealerRepository;
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
        this.dealerServicePurchaseOrderIdGenerator = dealerServicePurchaseOrderIdGenerator;
        this.dealerServicePurchaseAmountService = dealerServicePurchaseAmountService;
        this.wechatPayPaymentOrderFactory = wechatPayPaymentOrderFactory;
        this.wechatPayPaymentOrderRepository = wechatPayPaymentOrderRepository;
        this.wechatPayService = wechatPayService;
    }

    @Transactional
    public void purchaseService(
            PurchaseServiceCommand command,
            PurchaseServiceCommandResult commandResult) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());

        // 只能有一个处理中的订单的规则不放到 dealer.purchaseService 中（即不为此创建领域服务再作为参数传递给 dealer.purchaseService）
        // 只让 dealer.purchaseService 负责订单的创建，减少 dealer 的依赖，也是 dealer 的职责更单一

        boolean existsInProcessing = dealerServicePurchaseOrderRepository.existsInProcessing(command.getDealerId());
        CheckUtils.isTrue(
                !existsInProcessing,
                BusinessRuleException.class,
                "存在未完成的订单"
        );

        DealerServicePurchaseOrder dealerServicePurchaseOrder = dealer.purchaseService(
                dealerServicePurchaseAmountService,
                dealerServicePurchaseOrderIdGenerator
        );
        dealerServicePurchaseOrderRepository.save(dealerServicePurchaseOrder);

        commandResult.resultingPurchaseServiceOrderId(dealerServicePurchaseOrder.id().getId());
    }

    @Transactional
    public void requestPayPurchaseServiceOrder(
            RequestPayPurchaseServiceOrderCommand command,
            PayCommandResult commandResult) {
        CheckUtils.notNull(command, "command 必须不为 null");

        DealerServicePurchaseOrder order = existingOrder(command.getDealerServicePurchaseOrderId());

        if (PaymentChannelType.WECHAT_PAY.equals(command.getPaymentChannel())) {
            WechatPayPaymentOrder paymentOrder = wechatPayPaymentOrderFactory.createPaymentOrder(
                    order,
                    command.getPaymentMethod(),
                    command.getWechatOpenId()
            );
            wechatPayPaymentOrderRepository.save(paymentOrder);

            if (PaymentMethodType.WECHAT_PAY_JSAPI.equals(command.getPaymentMethod())) {
                commandResult.resultingWechatPayPrepayId(paymentOrder.createTransaction(wechatPayService));
                return;
            }

            throw new BusinessRuleException("支付类型暂不支持");
        }

        throw new BusinessRuleException("支付渠道暂不支持");
    }

    @Transactional
    public void notifyPurchaseServiceOrderPayment() {

    }

    @Transactional
    public void refundPurchaseServiceOrder() {

    }

    @Transactional
    public void notifyPurchaseServiceOrderRefund() {

    }

    private DealerServicePurchaseOrder existingOrder(DealerServicePurchaseOrderId id) {
        DealerServicePurchaseOrder order = dealerServicePurchaseOrderRepository.findById(id);

        CheckUtils.notNull(
                order,
                "DealerServicePurchaseOrderId({0})无效",
                id
        );

        return order;
    }

    private Dealer existingDealer(DealerId dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId);
        if (dealer == null) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Dealer({0}) 不存在", dealerId)
            );
        }
        return dealer;
    }
}
