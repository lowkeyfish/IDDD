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
import com.yujunyang.iddd.dealer.domain.car.ModelId;
import com.yujunyang.iddd.dealer.domain.car.Variant;
import com.yujunyang.iddd.dealer.domain.car.VariantId;
import com.yujunyang.iddd.dealer.domain.car.VariantService;
import org.springframework.stereotype.Service;

@Service
public class MockVariantService implements VariantService {
    private static final List<Variant> MOCK_VARIANTS = Arrays.asList(
            new Variant(
                    new VariantId(IdUtils.newId()),
                    "Mock_Variant_1",
                    125000,
                    new BrandId(IdUtils.newId()),
                    new ManufacturerId(IdUtils.newId()),
                    new ModelId(IdUtils.newId())
            )
    );

    @Override
    public Variant findById(VariantId variantId) {
        return MOCK_VARIANTS.stream().filter(n -> n.getId().equals(variantId)).findFirst().orElse(null);
    }
}
