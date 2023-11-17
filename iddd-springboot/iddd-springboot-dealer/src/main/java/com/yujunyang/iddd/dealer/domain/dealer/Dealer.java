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

import java.time.LocalDateTime;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.car.BrandId;

public class Dealer {
    private DealerId id;
    private String name;
    private Address address;
    private String telephone;
    private BrandId brandId;
    private LocalDateTime createTime;
    private DealerVisibilityStatusType visibilityStatus;
    private DealerServiceStatusType serviceStatus;
    private LocalDateTime serviceExpiryTime;


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
                DealerVisibilityStatusType.VISIBLE,
                DealerServiceStatusType.EXPIRED,
                null);

        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notNull(address, "address 必须不为 null");
        CheckUtils.notBlank(telephone, "telephone 必须不为空");
        CheckUtils.notNull(brandId, "brandId 必须不为 null");

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
            DealerVisibilityStatusType visibilityStatus,
            DealerServiceStatusType serviceStatus,
            LocalDateTime serviceExpiryTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.brandId = brandId;
        this.createTime = createTime;
        this.visibilityStatus = visibilityStatus;
        this.serviceStatus = serviceStatus;
        this.serviceExpiryTime = serviceExpiryTime;
    }

    public void changeName(
            String name,
            DealerNameUniquenessCheckService dealerNameUniquenessCheckService) {
        CheckUtils.isTrue(
                serviceStatus.equals(DealerServiceStatusType.IN_SERVICE),
                new BusinessRuleException(
                        "服务已到期",
                        ImmutableMap.of(
                                "dealerId",
                                id,
                                "serviceStatus",
                                serviceStatus
                        )
                )
        );

        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.isTrue(
                dealerNameUniquenessCheckService.isNameNotUsed(name, id),
                new BusinessRuleException(
                        "name 已被使用",
                        ImmutableMap.of(
                                "dealerId",
                                id,
                                "name",
                                name
                        )
                )
        );

        this.name = name;

        DomainEventPublisher.instance().publish(new DealerInfoUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void changeAddress(Address address) {
        CheckUtils.isTrue(
                serviceStatus.equals(DealerServiceStatusType.IN_SERVICE),
                new BusinessRuleException(
                        "服务已到期",
                        ImmutableMap.of(
                                "dealerId",
                                id,
                                "serviceStatus",
                                serviceStatus
                        )
                )
        );

        CheckUtils.notNull(address, "address 必须不为 null");

        this.address = address;

        DomainEventPublisher.instance().publish(new DealerInfoUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void changeTelephone(String telephone) {
        CheckUtils.isTrue(
                serviceStatus.equals(DealerServiceStatusType.IN_SERVICE),
                new BusinessRuleException(
                        "服务已到期",
                        ImmutableMap.of(
                                "dealerId",
                                id,
                                "serviceStatus",
                                serviceStatus
                        )
                )
        );

        CheckUtils.notBlank(telephone, "telephone 必须不为空");

        this.telephone = telephone;

        DomainEventPublisher.instance().publish(new DealerInfoUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void makeDealerVisible() {
        if (visibilityStatus.equals(DealerVisibilityStatusType.HIDDEN)) {
            return;
        }

        visibilityStatus = DealerVisibilityStatusType.HIDDEN;

        DomainEventPublisher.instance().publish(new DealerVisibilityChanged(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void makeDealerHidden() {
        if (visibilityStatus.equals(DealerVisibilityStatusType.VISIBLE)) {
            return;
        }

        visibilityStatus = DealerVisibilityStatusType.VISIBLE;

        DomainEventPublisher.instance().publish(new DealerVisibilityChanged(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public TimeRange nextServicePeriod() {
        LocalDateTime servicePeriodBegin;
        if (DealerServiceStatusType.IN_SERVICE.equals(serviceStatus)) {
            servicePeriodBegin = serviceExpiryTime;
        } else {
            servicePeriodBegin = LocalDateTime.now();
        }
        LocalDateTime servicePeriodEnd = servicePeriodBegin.plusYears(1);

        TimeRange nextServicePeriod = new TimeRange(servicePeriodBegin, servicePeriodEnd);
        return nextServicePeriod;
    }

    public void updateServiceTime(LocalDateTime nextServiceExpiryTime) {
        CheckUtils.notNull(nextServiceExpiryTime, "serviceExpiryTime 必须不为 null");

        boolean changed = false;

        if (serviceExpiryTime == null || nextServiceExpiryTime.isAfter(serviceExpiryTime)) {
            serviceExpiryTime = nextServiceExpiryTime;
            changed = true;
        }

        if (LocalDateTime.now().isBefore(serviceExpiryTime)) {
            serviceStatus = DealerServiceStatusType.IN_SERVICE;
            changed = true;
        }

        if (changed) {
            DomainEventPublisher.instance().publish(new DealerServiceChanged(
                    DateTimeUtilsEnhance.epochMilliSecond(),
                    id.getId()
            ));
        }
    }

    public DealerId id() {
        return id;
    }

    public boolean isInService() {
        return DealerServiceStatusType.IN_SERVICE.equals(serviceStatus);
    }

    public LocalDateTime serviceExpiryTime() {
        return serviceExpiryTime;
    }

    public DealerSnapshot snapshot() {
        return new DealerSnapshot(
                id,
                name,
                telephone,
                address,
                brandId,
                createTime,
                visibilityStatus,
                serviceStatus,
                serviceExpiryTime
        );
    }

}
