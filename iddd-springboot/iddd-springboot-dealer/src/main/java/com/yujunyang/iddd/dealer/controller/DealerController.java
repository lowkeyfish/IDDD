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

import com.yujunyang.iddd.common.data.RestResponse;
import com.yujunyang.iddd.dealer.application.DealerApplicationService;
import com.yujunyang.iddd.dealer.application.command.DealerActivationCommand;
import com.yujunyang.iddd.dealer.application.command.DealerCreateCommand;
import com.yujunyang.iddd.dealer.controller.input.DealerCreateRequestBody;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dealers")
public class DealerController {
    private DealerApplicationService dealerApplicationService;

    @Autowired
    public DealerController(
            DealerApplicationService dealerApplicationService) {
        this.dealerApplicationService = dealerApplicationService;
    }

    @PostMapping("")
    public RestResponse<Long> createDealer(
            @RequestBody DealerCreateRequestBody requestBody) {
        RestResponse<Long> response = new RestResponse<>();

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

        return response;
    }

    @DeleteMapping("{dealerId}/activation")
    public RestResponse deactivateDealer(@PathVariable("dealerId") long dealerId) {
        RestResponse<Long> response = new RestResponse<>();

        dealerApplicationService.deactivate(
                new DealerActivationCommand(
                        DealerId.parse(dealerId)
                )
        );

        return response;
    }

    @PutMapping("{dealerId}/activation")
    public RestResponse activateDealer(@PathVariable("dealerId") long dealerId) {
        RestResponse<Long> response = new RestResponse<>();

        dealerApplicationService.activate(
                new DealerActivationCommand(
                        DealerId.parse(dealerId)
                )
        );

        return response;
    }
}
