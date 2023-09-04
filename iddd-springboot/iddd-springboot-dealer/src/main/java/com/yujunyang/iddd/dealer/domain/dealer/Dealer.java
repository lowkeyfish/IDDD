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

package com.yujunyang.iddd.dealer.domain.dealer;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.car.BrandId;

public class Dealer {
    private DealerId id;
    private String name;
    private Address address;
    private String telephone;
    private BrandId brandId;
    private LocalDateTime createTime;
    private DealerStatusType status;

    public Dealer(
            DealerId id,
            String name,
            Address address,
            String telephone,
            BrandId brandId) {

        this(
                id,
                name,
                address,
                telephone,
                brandId,
                LocalDateTime.now(),
                DealerStatusType.ENABLED
        );

        DomainEventPublisher.instance().publish(new DealerCreated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public Dealer(
            DealerId id,
            String name,
            Address address,
            String telephone,
            BrandId brandId,
            LocalDateTime createTime,
            DealerStatusType status) {
        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notNull(address, "address 必须不为 null");
        CheckUtils.notBlank(telephone, "telephone 必须不为空");
        CheckUtils.notNull(brandId, "brandId 必须不为 null");
        CheckUtils.notNull(createTime, "createTime 必须不为 null");
        CheckUtils.notNull(status, "status 必须不为 null");

        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.brandId = brandId;
        this.createTime = createTime;
        this.status = status;
    }

    public DealerId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public DealerStatusType getStatus() {
        return status;
    }

    public void changeName(
            String name,
            DealerNameUniquenessCheckService dealerNameUniquenessCheckService) {
        if (DealerStatusType.DISABLED.equals(status)) {
            throw new UnsupportedOperationException("Dealer 被禁用");
        }

        CheckUtils.notBlank(name, "name 必须不为空");
        if (!dealerNameUniquenessCheckService.isNameNotUsed(name, id)) {
            throw new IllegalArgumentException(
                    MessageFormat.format(
                            "name({0}) 已被使用",
                            name
                    )
            );
        }

        this.name = name;

        DomainEventPublisher.instance().publish(new DealerUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void changeAddress(Address address) {
        if (DealerStatusType.DISABLED.equals(status)) {
            throw new UnsupportedOperationException("Dealer 被禁用");
        }

        CheckUtils.notNull(address, "address 必须不为 null");

        this.address = address;

        DomainEventPublisher.instance().publish(new DealerUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void changeTelephone(String telephone) {
        if (DealerStatusType.DISABLED.equals(status)) {
            throw new UnsupportedOperationException("Dealer 被禁用");
        }

        CheckUtils.notBlank(telephone, "telephone 必须不为空");

        this.telephone = telephone;

        DomainEventPublisher.instance().publish(new DealerUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void disable() {
        if (DealerStatusType.DISABLED.equals(status)) {
            throw new UnsupportedOperationException("Dealer 当前已是禁用状态");
        }

        status = DealerStatusType.DISABLED;

        DomainEventPublisher.instance().publish(new DealerDisabled(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void enable() {
        if (DealerStatusType.ENABLED.equals(status)) {
            throw new UnsupportedOperationException("Dealer 当前已是启用状态");
        }

        status = DealerStatusType.ENABLED;

        DomainEventPublisher.instance().publish(new DealerEnabled(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }
}
