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

package com.yujunyang.iddd.dealer.application;

import java.text.MessageFormat;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.IdUtils;
import com.yujunyang.iddd.dealer.application.command.DealerChangeAddressCommand;
import com.yujunyang.iddd.dealer.application.command.DealerChangeNameCommand;
import com.yujunyang.iddd.dealer.application.command.DealerChangeTelephoneCommand;
import com.yujunyang.iddd.dealer.application.command.DealerCreateCommand;
import com.yujunyang.iddd.dealer.application.command.DealerDisableCommand;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerFactory;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerNameUniquenessCheckService;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerApplicationService {
    private DealerNameUniquenessCheckService dealerNameUniquenessCheckService;
    private DealerRepository dealerRepository;
    private DealerFactory dealerFactory;

    @Autowired
    public DealerApplicationService(
            DealerNameUniquenessCheckService dealerNameUniquenessCheckService,
            DealerRepository dealerRepository,
            DealerFactory dealerFactory) {
        this.dealerNameUniquenessCheckService = dealerNameUniquenessCheckService;
        this.dealerRepository = dealerRepository;
        this.dealerFactory = dealerFactory;
    }

    @Transactional
    public DealerId create(DealerCreateCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = dealerFactory.createDealer(
                command.getName(),
                command.getCityId(),
                command.getSpecificAddress(),
                command.getTelephone(),
                command.getBrandId()
        );
        dealerRepository.save(dealer);

        return dealer.getId();
    }

    @Transactional
    public void changeName(DealerChangeNameCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = findById(command.getDealerId());
        dealer.changeName(command.getName(), dealerNameUniquenessCheckService);

        dealerRepository.save(dealer);
    }

    @Transactional
    public void changeTelephone(DealerChangeTelephoneCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = findById(command.getDealerId());
        dealer.changeTelephone(command.getTelephone());

        dealerRepository.save(dealer);
    }

    @Transactional
    public void changeAddress(DealerChangeAddressCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = findById(command.getDealerId());
        dealer.changeAddress(new Address(
                command.getCityId(),
                command.getSpecificAddress()
        ));

        dealerRepository.save(dealer);
    }

    @Transactional
    public void disable(DealerDisableCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = findById(command.getDealerId());
        dealer.disable();

        dealerRepository.save(dealer);
    }

    @Transactional
    public void enable(DealerDisableCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = findById(command.getDealerId());
        dealer.enable();

        dealerRepository.save(dealer);
    }

    private Dealer findById(DealerId dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId);
        if (dealer == null) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Dealer({0}) 不存在", dealerId)
            );
        }
        return dealer;
    }
}
