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

package com.yujunyang.iddd.dealer.infrastructure.remote.car;

import java.util.List;

import com.yujunyang.iddd.common.data.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "api-car", url = "${api-car.url}")
public interface CarApi {
    @GetMapping("/brands/{brandId}")
    RestResponse<BrandResponseData> findBrandById(
            @PathVariable("brandId") long brandId);

    @GetMapping("/brands")
    RestResponse<List<BrandResponseData>> queryBrands(
            @RequestParam("name") String name);

    @GetMapping("/models/{modelId}")
    RestResponse<ModelResponseData> findModelById(
            @PathVariable("modelId") long modelId);

    @GetMapping("/variants/{variantId}")
    RestResponse<VariantResponseData> findVariantById(
            @PathVariable("variantId") long variantId);
}
