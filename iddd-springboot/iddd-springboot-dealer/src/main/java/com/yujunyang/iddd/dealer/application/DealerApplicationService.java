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
import com.yujunyang.iddd.common.utils.StringUtilsEnhance;
import com.yujunyang.iddd.dealer.application.command.DealerCreateCommand;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
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

    @Autowired
    public DealerApplicationService(
            DealerNameUniquenessCheckService dealerNameUniquenessCheckService,
            DealerRepository dealerRepository) {
        this.dealerNameUniquenessCheckService = dealerNameUniquenessCheckService;
        this.dealerRepository = dealerRepository;
    }

    @Transactional
    public DealerId create(DealerCreateCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        boolean nameNotUsed = dealerNameUniquenessCheckService.isNameNotUsed(command.getName());
        if (!nameNotUsed) {
            throw new IllegalArgumentException(
                    MessageFormat.format("name({0}) 已被使用", command.getName())
            );
        }

        Dealer dealer = new Dealer(
                new DealerId(IdUtils.newId()),
                command.getName(),
                new Address(
                        command.getCityId(),
                        command.getSpecificAddress()
                ),
                command.getTelephone(),
                command.getBrandId()
        );
        dealerRepository.save(dealer);

        return dealer.getId();
    }
}