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

package com.yujunyang.iddd.dealer.infrastructure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.yujunyang.iddd.common.data.RestResponse;
import com.yujunyang.iddd.common.exception.ApiResponseException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.car.Brand;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.car.BrandService;
import com.yujunyang.iddd.dealer.infrastructure.remote.car.BrandResponseData;
import com.yujunyang.iddd.dealer.infrastructure.remote.car.CarApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoteApiBrandService implements BrandService {
    private CarApi carApi;

    @Autowired
    public RemoteApiBrandService(
            CarApi carApi) {
        this.carApi = carApi;
    }

    @Override
    public Brand findById(BrandId brandId) {
        RestResponse<BrandResponseData> response = carApi.findBrandById(brandId.getId());
        CheckUtils.isTrue(
            response.getCode() == 0,
                ApiResponseException.class,
                "查询单个品牌接口调用失败,BrandId({0}),code({1}),message({2})",
                brandId,
                response.getCode(),
                response.getMessage()
        );

        return Optional.ofNullable(response.getData()).map(n -> convert(n)).orElse(null);
    }

    @Override
    public Brand findByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        RestResponse<List<BrandResponseData>> response = carApi.queryBrands(name);
        CheckUtils.isTrue(
                response.getCode() == 0,
                ApiResponseException.class,
                "查询品牌接口调用失败,name({0}),code({1}),message({2})",
                name,
                response.getCode(),
                response.getMessage()
        );
        return Optional.ofNullable(response.getData()).orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .map(n -> new Brand(
                        new BrandId(n.getId()),
                        n.getName(),
                        n.getLogo(),
                        n.getFirstLetter()
                ))
                .orElse(null);
    }

    private Brand convert(BrandResponseData responseData) {
        return new Brand(
                BrandId.parse(responseData.getId()),
                responseData.getName(),
                responseData.getLogo(),
                responseData.getFirstLetter()
        );
    }
}
