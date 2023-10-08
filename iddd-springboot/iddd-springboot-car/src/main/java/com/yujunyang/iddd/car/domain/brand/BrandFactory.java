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

package com.yujunyang.iddd.car.domain.brand;

import com.yujunyang.iddd.common.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandFactory {
    private BrandRepository brandRepository;
    private BrandNameUniquenessCheckService brandNameUniquenessCheckService;

    @Autowired
    public BrandFactory(
            BrandRepository brandRepository,
            BrandNameUniquenessCheckService brandNameUniquenessCheckService) {
        this.brandRepository = brandRepository;
        this.brandNameUniquenessCheckService = brandNameUniquenessCheckService;
    }

    public Brand createBrand(
            String name,
            String logo,
            String firstLetter) {
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(logo, "logo 必须不为空");
        CheckUtils.notBlank(firstLetter, "firstLetter 必须不为空");

        CheckUtils.isTrue(
                brandNameUniquenessCheckService.isNameNotUsed(name),
                "name({0}) 已存在品牌",
                name
        );

        Brand brand = new Brand(brandRepository.nextId(), name, logo, firstLetter);
        return brand;
    }
}
