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

package com.yujunyang.iddd.dealer.application.data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerServiceStatusType;
import com.yujunyang.iddd.dealer.domain.dealer.DealerVisibilityStatusType;

public class DealerViewModel {
    private DealerId id;
    private String name;
    private Address address;
    private String telephone;
    private BrandId brandId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createTime;
    private DealerVisibilityStatusType visibilityStatus;
    private DealerServiceStatusType serviceStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime serviceExpiryTime;

    public DealerId getId() {
        return id;
    }

    public void setId(DealerId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public DealerVisibilityStatusType getVisibilityStatus() {
        return visibilityStatus;
    }

    public void setVisibilityStatus(DealerVisibilityStatusType visibilityStatus) {
        this.visibilityStatus = visibilityStatus;
    }

    public DealerServiceStatusType getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(DealerServiceStatusType serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public LocalDateTime getServiceExpiryTime() {
        return serviceExpiryTime;
    }

    public void setServiceExpiryTime(LocalDateTime serviceExpiryTime) {
        this.serviceExpiryTime = serviceExpiryTime;
    }
}
