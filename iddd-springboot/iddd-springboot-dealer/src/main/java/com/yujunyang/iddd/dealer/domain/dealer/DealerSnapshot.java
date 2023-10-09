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

import java.time.LocalDateTime;

import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.common.TimeRange;

public class DealerSnapshot {
    private DealerId id;
    private String name;
    private String telephone;
    private Address address;
    private BrandId brandId;
    private LocalDateTime createTime;
    private DealerActivationStatusType activationStatus;

    public DealerSnapshot(
            DealerId id,
            String name,
            String telephone,
            Address address,
            BrandId brandId,
            LocalDateTime createTime,
            DealerActivationStatusType activationStatus) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.address = address;
        this.brandId = brandId;
        this.createTime = createTime;
        this.activationStatus = activationStatus;
    }

    public DealerId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public Address getAddress() {
        return address;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public DealerActivationStatusType getActivationStatus() {
        return activationStatus;
    }
}
