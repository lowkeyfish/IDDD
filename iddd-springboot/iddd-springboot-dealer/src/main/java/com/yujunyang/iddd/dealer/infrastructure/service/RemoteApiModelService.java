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

import java.util.Arrays;
import java.util.List;

import com.yujunyang.iddd.common.utils.IdUtils;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.car.ManufacturerId;
import com.yujunyang.iddd.dealer.domain.car.Model;
import com.yujunyang.iddd.dealer.domain.car.ModelId;
import com.yujunyang.iddd.dealer.domain.car.ModelService;
import org.springframework.stereotype.Service;

@Service
public class RemoteApiModelService implements ModelService {
    private static final List<Model> MOCK_MODELS = Arrays.asList(
            new Model(
                    new ModelId(IdUtils.newId()),
                    new BrandId(IdUtils.newId()),
                    new ManufacturerId(IdUtils.newId()),
                    "Mock_Model_1",
                    "/images/model/a6l.png",
                    230000,
                    330000
            )
    );

    @Override
    public Model findById(ModelId modelId) {
        return MOCK_MODELS.stream().filter(n -> n.getId().equals(modelId)).findFirst().orElse(null);
    }
}
