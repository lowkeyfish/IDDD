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
import java.util.List;

import com.yujunyang.iddd.common.domain.event.DomainEventRabbitMQSyncPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.application.command.DealerServiceApproachingExpiryRemindCommand;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServiceApproachingExpiryRemindRequested;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServiceApproachingExpiryRemindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerServiceApproachingExpiryRemindApplicationService {
    private DomainEventRabbitMQSyncPublisher domainEventRabbitMQSyncPublisher;
    private AllDealerIdQueryService allDealerIdQueryService;
    private DealerRepository dealerRepository;
    private DealerServiceApproachingExpiryRemindService dealerServiceApproachingExpiryRemindService;

    @Autowired
    public DealerServiceApproachingExpiryRemindApplicationService(
            DomainEventRabbitMQSyncPublisher domainEventRabbitMQSyncPublisher,
            AllDealerIdQueryService allDealerIdQueryService,
            DealerRepository dealerRepository,
            DealerServiceApproachingExpiryRemindService dealerServiceApproachingExpiryRemindService) {
        this.domainEventRabbitMQSyncPublisher = domainEventRabbitMQSyncPublisher;
        this.allDealerIdQueryService = allDealerIdQueryService;
        this.dealerRepository = dealerRepository;
        this.dealerServiceApproachingExpiryRemindService = dealerServiceApproachingExpiryRemindService;
    }


    public void startAllDealerServiceApproachingExpiryRemind() {
        List<DealerId> dealerIds = allDealerIdQueryService.allDealerId();
        dealerIds.forEach(dealerId -> domainEventRabbitMQSyncPublisher.publish(
                new DealerServiceApproachingExpiryRemindRequested(
                        DateTimeUtilsEnhance.epochMilliSecond(),
                        dealerId.getId()
                ))
        );
    }

    @Transactional
    public void dealerServiceApproachingExpiryRemind(
            DealerServiceApproachingExpiryRemindCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());
        dealerServiceApproachingExpiryRemindService.remind(dealer);
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
