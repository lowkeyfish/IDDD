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

import java.util.Arrays;

import com.rabbitmq.client.Channel;
import com.yujunyang.iddd.common.rabbitmq.AbstractRabbitMQListener;
import com.yujunyang.iddd.common.utils.JacksonUtils;
import com.yujunyang.iddd.dealer.application.DealerApplicationService;
import com.yujunyang.iddd.dealer.application.DealerQueryService;
import com.yujunyang.iddd.dealer.application.DealerServicePurchaseApplicationService;
import com.yujunyang.iddd.dealer.application.command.UpdateServiceTimeOnServicePurchaseOrderPaymentSuccessCommand;
import com.yujunyang.iddd.dealer.config.rabbitmq.RabbitMQConfig;
import com.yujunyang.iddd.dealer.domain.dealer.DealerCreated;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerServiceChanged;
import com.yujunyang.iddd.dealer.domain.dealer.DealerUpdated;
import com.yujunyang.iddd.dealer.domain.dealer.DealerVisibilityChanged;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderId;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderPaid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DealerQueueListener extends AbstractRabbitMQListener {
    private static final Logger LOGGER = LogManager.getLogger();

    private DealerQueryService dealerQueryService;
    private DealerServicePurchaseApplicationService dealerServicePurchaseApplicationService;
    private DealerApplicationService dealerApplicationService;

    @Autowired
    public DealerQueueListener(
            RabbitMQConfig rabbitMQConfig,
            DealerQueryService dealerQueryService,
            DealerServicePurchaseApplicationService dealerServicePurchaseApplicationService,
            DealerApplicationService dealerApplicationService) {
        super(
                rabbitMQConfig.internalDealerQueueName,
                Arrays.asList("*"),
                LOGGER
        );
        this.dealerQueryService = dealerQueryService;
        this.dealerServicePurchaseApplicationService = dealerServicePurchaseApplicationService;
        this.dealerApplicationService = dealerApplicationService;
    }

    @RabbitListener(
            queues = "#{rabbitMQConfig.internalDealerQueueName}",
            concurrency = "1-5"
    )
    @Transactional
    public void onMessage(Message message, Channel channel) {
        onMessageWrap(message, channel);
    }


    @Override
    protected void messageHandler(String id, String type, String body) {
        if (DealerCreated.class.getSimpleName().equals(type)) {
            DealerCreated domainEvent = JacksonUtils.deSerialize(body, DealerCreated.class);
            dealerQueryService.refreshDealerCache(new DealerId(domainEvent.getDealerId()));
        } else if (DealerUpdated.class.getSimpleName().equals(type)) {
            DealerUpdated domainEvent = JacksonUtils.deSerialize(body, DealerUpdated.class);
            dealerQueryService.refreshDealerCache(new DealerId(domainEvent.getDealerId()));
        } else if (DealerVisibilityChanged.class.getSimpleName().equals(type)) {
            DealerVisibilityChanged domainEvent = JacksonUtils.deSerialize(body, DealerVisibilityChanged.class);
            dealerQueryService.refreshDealerCache(new DealerId(domainEvent.getDealerId()));
        } else if (DealerServiceChanged.class.getSimpleName().equals(type)) {
            DealerServiceChanged domainEvent = JacksonUtils.deSerialize(body, DealerServiceChanged.class);
            dealerQueryService.refreshDealerCache(new DealerId(domainEvent.getDealerId()));
        } else if (DealerServicePurchaseOrderPaid.class.getSimpleName().equals(type)) {
            DealerServicePurchaseOrderPaid domainEvent =
                    JacksonUtils.deSerialize(body, DealerServicePurchaseOrderPaid.class);
            dealerApplicationService.updateServiceTimeOnServicePurchaseOrderPaymentSuccess(
                    new UpdateServiceTimeOnServicePurchaseOrderPaymentSuccessCommand(
                            new DealerServicePurchaseOrderId(domainEvent.getDealerServicePurchaseOrderId())
                    )
            );
        } else {
            LOGGER.warn("{0}不支持处理{1}", DealerQueueListener.class.getSimpleName(), type);
        }
    }
}
