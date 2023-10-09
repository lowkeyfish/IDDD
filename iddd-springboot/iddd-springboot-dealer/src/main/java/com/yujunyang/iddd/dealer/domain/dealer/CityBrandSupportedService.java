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

package com.yujunyang.iddd.dealer.domain.dealer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.address.City;
import com.yujunyang.iddd.dealer.domain.address.CityService;
import com.yujunyang.iddd.dealer.domain.car.Brand;
import com.yujunyang.iddd.dealer.domain.car.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityBrandSupportedService {
    private CityService cityService;
    private BrandService brandService;

    @Autowired
    public CityBrandSupportedService(
            CityService cityService,
            BrandService brandService) {
        this.cityService = cityService;
        this.brandService = brandService;
    }

    public List<City> supportedCities() {
        return Arrays.asList(cityService.findByName("北京"))
                .stream()
                .filter(n -> n != null)
                .collect(Collectors.toList());
    }

    public List<Brand> supportedBrands() {
        return Arrays.asList(brandService.findByName("奥迪"))
                .stream()
                .filter(n -> n != null)
                .collect(Collectors.toList());
    }

    public void check(City city, Brand brand) {
        CheckUtils.notNull(city, "city 必须不为 null");
        CheckUtils.notNull(brand, "brand 必须不为 null");

        CheckUtils.isTrue(
                supportedCities().stream().anyMatch(n -> n.getId().equals(city.getId())),
                "城市({0}:{1})暂不支持",
                city.getId(),
                city.getName()
        );

        CheckUtils.isTrue(
                supportedBrands().stream().anyMatch(n -> n.getId().equals(brand.getId())),
                "品牌({0}：{1})暂不支持",
                brand.getId(),
                brand.getName()
        );
    }
}
