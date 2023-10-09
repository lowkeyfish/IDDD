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

import java.util.Optional;

import com.yujunyang.iddd.common.data.RestResponse;
import com.yujunyang.iddd.common.exception.ApiResponseException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.car.ManufacturerId;
import com.yujunyang.iddd.dealer.domain.car.Model;
import com.yujunyang.iddd.dealer.domain.car.ModelId;
import com.yujunyang.iddd.dealer.domain.car.ModelService;
import com.yujunyang.iddd.dealer.infrastructure.remote.car.BrandResponseData;
import com.yujunyang.iddd.dealer.infrastructure.remote.car.CarApi;
import com.yujunyang.iddd.dealer.infrastructure.remote.car.ModelResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoteApiModelService implements ModelService {
    private CarApi carApi;

    @Autowired
    public RemoteApiModelService(
            CarApi carApi) {
        this.carApi = carApi;
    }

    @Override
    public Model findById(ModelId modelId) {
        RestResponse<ModelResponseData> response = carApi.findModelById(modelId.getId());
        CheckUtils.isTrue(
                response.getCode() == 0,
                ApiResponseException.class,
                "查询单个车系接口调用失败,ModelId({0}),code({1}),message({2})",
                modelId,
                response.getCode(),
                response.getMessage()
        );

        return Optional.ofNullable(response.getData()).map(n -> convert(n)).orElse(null);
    }

    private Model convert(ModelResponseData responseData) {
        return new Model(
                ModelId.parse(responseData.getId()),
                BrandId.parse(responseData.getBrandId()),
                ManufacturerId.parse(responseData.getManufacturerId()),
                responseData.getName(),
                responseData.getImage(),
                responseData.getPriceMin(),
                responseData.getPriceMax()
        );
    }
}
