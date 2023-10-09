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

package com.yujunyang.iddd.dealer.controller;

import javax.annotation.Resource;

import com.yujunyang.iddd.dealer.application.DealerApplicationService;
import com.yujunyang.iddd.dealer.application.command.DealerCreateCommand;
import com.yujunyang.iddd.dealer.application.data.DealerCreateCommandResult;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.address.ProvinceId;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private DealerApplicationService dealerApplicationService;
    @Resource
    private DealerRepository dealerRepository;

    @PostMapping("createDealer")
    public Long createDealer() {
        DealerCreateCommand command = new DealerCreateCommand(
                "Dealer20230903",
                new CityId(110100),
                "海淀区丹棱街3号",
                "010-1234567",
                new BrandId(1L)
        );

        final Long[] newDealerId = new Long[1];
        dealerApplicationService.create(
                command,
                dealerId -> newDealerId[0] = dealerId);
        return newDealerId[0];
    }

    @PostMapping("dealer")
    public Dealer dealer(long dealerId) {
        return dealerRepository.findById(new DealerId(dealerId));
    }
}
