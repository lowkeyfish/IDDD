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

package com.yujunyang.iddd.dealer.listener;

import java.text.MessageFormat;
import java.util.Arrays;

import com.rabbitmq.client.Channel;
import com.yujunyang.iddd.common.rabbitmq.AbstractRabbitMQListener;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.JacksonUtils;
import com.yujunyang.iddd.dealer.application.DealerApplicationService;
import com.yujunyang.iddd.dealer.application.DealerServicePurchaseOrderApplicationService;
import com.yujunyang.iddd.dealer.application.command.OrderPaymentStatusChangeCommand;
import com.yujunyang.iddd.dealer.application.command.UpdateServiceTimeOnServicePurchaseOrderPaidCommand;
import com.yujunyang.iddd.dealer.config.rabbitmq.RabbitMQConfig;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.order.OrderPaid;
import com.yujunyang.iddd.dealer.domain.order.OrderType;
import com.yujunyang.iddd.dealer.domain.payment.AbstractPaymentDomainEvent;
import com.yujunyang.iddd.dealer.domain.payment.PaymentFailed;
import com.yujunyang.iddd.dealer.domain.payment.PaymentInitiated;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrderId;
import com.yujunyang.iddd.dealer.domain.payment.PaymentPaid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DealerServicePurchaseOrderQueueListener extends AbstractRabbitMQListener {
    private static final Logger LOGGER = LogManager.getLogger();

    private DealerServicePurchaseOrderApplicationService dealerServicePurchaseOrderApplicationService;
    private DealerApplicationService dealerApplicationService;


    @Autowired
    public DealerServicePurchaseOrderQueueListener(
            RabbitMQConfig rabbitMQConfig,
            DealerServicePurchaseOrderApplicationService dealerServicePurchaseOrderApplicationService,
            DealerApplicationService dealerApplicationService) {
        super(
                rabbitMQConfig.internalDealerServicePurchaseOrderQueueName,
                Arrays.asList("*"),
                LOGGER
        );
        this.dealerServicePurchaseOrderApplicationService = dealerServicePurchaseOrderApplicationService;
        this.dealerApplicationService = dealerApplicationService;
    }

    @RabbitListener(
            queues = "#{rabbitMQConfig.internalDealerServicePurchaseOrderQueueName}",
            concurrency = "1-5"
    )
    @Transactional
    public void onMessage(Message message, Channel channel) {
        onMessageWrap(message, channel);
    }


    @Override
    protected void messageHandler(String id, String type, String body) {
        if (Arrays.asList(
                PaymentInitiated.class.getSimpleName(),
                PaymentPaid.class.getSimpleName(),
                PaymentFailed.class.getSimpleName()
        ).contains(type)) {
            AbstractPaymentDomainEvent domainEvent = deserializePaymentDomainEvent(type, body);

            CheckUtils.isTrue(
                    OrderType.DEALER_SERVICE_PURCHASE_ORDER.equals(domainEvent.getOrderType()),
                    RuntimeException.class,
                    "队列({0})接收了不属于({1})的事件,id({2}),type({3}),body({4})",
                    queueName,
                    OrderType.DEALER_SERVICE_PURCHASE_ORDER.routingKeySegment(),
                    id,
                    type,
                    body
            );

            OrderPaymentStatusChangeCommand command = new OrderPaymentStatusChangeCommand(
                    new PaymentOrderId(domainEvent.getPaymentOrderId()),
                    new DealerServicePurchaseOrderId(domainEvent.getOrderId())
            );

            if (PaymentInitiated.class.getSimpleName().equals(type)) {
                dealerServicePurchaseOrderApplicationService.markAsPaymentInitiated(command);
            } else if (PaymentPaid.class.getSimpleName().equals(type)) {
                dealerServicePurchaseOrderApplicationService.markAsPaid(command);
            } else if (PaymentFailed.class.getSimpleName().equals(type)) {
                dealerServicePurchaseOrderApplicationService.markAsPaymentFailed(command);
            }

            return;
        }

        if (Arrays.asList(
                OrderPaid.class.getSimpleName()
        ).contains(type)) {
            OrderPaid domainEvent = JacksonUtils.deserialize(body, OrderPaid.class);
            dealerApplicationService.updateServiceTimeOnServicePurchaseOrderPaid(
                    new UpdateServiceTimeOnServicePurchaseOrderPaidCommand(
                            new DealerServicePurchaseOrderId(domainEvent.getOrderId())
                    )
            );

            return;
        }

        LOGGER.warn("{0}不支持处理{1}", DealerServicePurchaseOrderQueueListener.class.getSimpleName(), type);
    }

    private AbstractPaymentDomainEvent deserializePaymentDomainEvent(String type, String json) {
        Class clazz;
        if (PaymentInitiated.class.getSimpleName().equals(type)) {
            clazz = PaymentInitiated.class;
        } else if (PaymentPaid.class.getSimpleName().equals(type)) {
            clazz = PaymentPaid.class;
        } else if (PaymentFailed.class.getSimpleName().equals(type)) {
            clazz = PaymentFailed.class;
        } else {
            throw new RuntimeException(
                    MessageFormat.format("type({0})不支持处理", type)
            );
        }

        return (AbstractPaymentDomainEvent) JacksonUtils.deserialize(json, clazz);
    }
}
