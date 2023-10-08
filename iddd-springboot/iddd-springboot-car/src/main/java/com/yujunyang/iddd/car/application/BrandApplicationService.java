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

package com.yujunyang.iddd.car.application;

import com.yujunyang.iddd.car.application.command.BrandCreateCommand;
import com.yujunyang.iddd.car.application.data.BrandCreateCommandResult;
import com.yujunyang.iddd.car.domain.brand.Brand;
import com.yujunyang.iddd.car.domain.brand.BrandFactory;
import com.yujunyang.iddd.car.domain.brand.BrandRepository;
import com.yujunyang.iddd.car.domain.brand.BrandStatusType;
import com.yujunyang.iddd.common.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrandApplicationService {
    private BrandRepository brandRepository;
    private BrandFactory brandFactory;

    @Autowired
    public BrandApplicationService(
            BrandRepository brandRepository,
            BrandFactory brandFactory) {
        this.brandRepository = brandRepository;
        this.brandFactory = brandFactory;
    }

    @Transactional
    public void createBrand(
            BrandCreateCommand command,
            BrandCreateCommandResult brandCreateCommandResult) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Brand brand = brandFactory.createBrand(
                command.getName(),
                command.getLogo(),
                command.getFirstLetter()
        );

        brandRepository.save(brand);

        brandCreateCommandResult.resultingBrandId(brand.id().getId());
    }
}
