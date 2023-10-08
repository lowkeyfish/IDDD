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

package com.yujunyang.iddd.car.controller;

import java.util.Arrays;
import java.util.List;

import com.yujunyang.iddd.car.application.BrandApplicationService;
import com.yujunyang.iddd.car.application.BrandQueryService;
import com.yujunyang.iddd.car.application.ModelQueryService;
import com.yujunyang.iddd.car.application.command.BrandCreateCommand;
import com.yujunyang.iddd.car.application.data.BrandViewModel;
import com.yujunyang.iddd.car.application.data.ModelViewModel;
import com.yujunyang.iddd.car.controller.input.BrandCreateRequestBody;
import com.yujunyang.iddd.car.domain.brand.BrandId;
import com.yujunyang.iddd.common.data.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brands")
public class BrandController {
    private BrandQueryService brandQueryService;
    private BrandApplicationService brandApplicationService;
    private ModelQueryService modelQueryService;

    @Autowired
    public BrandController(
            BrandQueryService brandQueryService,
            BrandApplicationService brandApplicationService,
            ModelQueryService modelQueryService) {
        this.brandQueryService = brandQueryService;
        this.brandApplicationService = brandApplicationService;
        this.modelQueryService = modelQueryService;
    }

    @PostMapping("")
    public RestResponse<Long> createBrand(
            @RequestBody BrandCreateRequestBody requestBody) {
        RestResponse<Long> response = new RestResponse<>();

        brandApplicationService.createBrand(
                new BrandCreateCommand(
                        requestBody.getName(),
                        requestBody.getLogo(),
                        requestBody.getFirstLetter()
                ),
                brandId -> response.setData(brandId)
        );

        return response;
    }

    @GetMapping("{brandId}")
    public RestResponse<BrandViewModel> findBrandById(
            @PathVariable("brandId") long brandId) {
        BrandViewModel brand = brandQueryService.findById(BrandId.parse(brandId));
        return new RestResponse<>(brand);
    }

    @GetMapping("")
    public RestResponse<List<BrandViewModel>> queryBrands() {
        List<BrandViewModel> brandList = brandQueryService.allBrand();
        return new RestResponse<>(brandList);
    }

    @GetMapping("{brandId}/models")
    public RestResponse<List<ModelViewModel>> findModelByBrandId(
            @PathVariable("brandId") long brandId) {
        return new RestResponse<>(modelQueryService.findByBrandIds(Arrays.asList(BrandId.parse(brandId))));
    }

}
