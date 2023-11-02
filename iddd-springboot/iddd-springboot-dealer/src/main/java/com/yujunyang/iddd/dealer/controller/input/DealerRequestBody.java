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

package com.yujunyang.iddd.dealer.controller.input;

import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.car.BrandId;

public class DealerRequestBody {
    private String name;
    private CityId cityId;
    private String specificAddress;
    private String telephone;
    private BrandId brandId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CityId getCityId() {
        return cityId;
    }

    public void setCityId(CityId cityId) {
        this.cityId = cityId;
    }

    public String getSpecificAddress() {
        return specificAddress;
    }

    public void setSpecificAddress(String specificAddress) {
        this.specificAddress = specificAddress;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public void setBrandId(BrandId brandId) {
        this.brandId = brandId;
    }
}
