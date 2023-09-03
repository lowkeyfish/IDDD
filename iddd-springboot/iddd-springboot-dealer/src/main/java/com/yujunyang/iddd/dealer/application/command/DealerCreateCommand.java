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

package com.yujunyang.iddd.dealer.application.command;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.car.BrandId;

public class DealerCreateCommand {
    private String name;
    private CityId cityId;
    private String specificAddress;
    private String telephone;
    private BrandId brandId;

    public DealerCreateCommand(
            String name,
            CityId cityId,
            String specificAddress,
            String telephone,
            BrandId brandId) {

        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(specificAddress, "specificAddress 必须不为空");
        CheckUtils.notBlank(telephone, "telephone 必须不为空");
        CheckUtils.notNull(brandId, "brandId 必须不为 null");
        CheckUtils.notNull(cityId, "cityId 必须不为 null");

        this.name = name;
        this.cityId = cityId;
        this.specificAddress = specificAddress;
        this.telephone = telephone;
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public CityId getCityId() {
        return cityId;
    }

    public String getSpecificAddress() {
        return specificAddress;
    }
}
