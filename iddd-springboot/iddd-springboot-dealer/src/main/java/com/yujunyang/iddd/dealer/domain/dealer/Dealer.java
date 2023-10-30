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

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.exception.InvalidStatusException;
import com.yujunyang.iddd.common.exception.NameNotUniqueException;
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
            DealerNameUniquenessCheckService dealerNameUniquenessCheckService)
            throws NameNotUniqueException, InvalidStatusException {
        CheckUtils.isTrue(
                serviceStatus.equals(DealerServiceStatusType.IN_SERVICE),
                new InvalidStatusException(
                        serviceStatus.getDescription(),
                        "Dealer 修改名称"
                )
        );

        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.isTrue(
                dealerNameUniquenessCheckService.isNameNotUsed(name, id),
                new NameNotUniqueException(name)
        );

        this.name = name;

        DomainEventPublisher.instance().publish(new DealerUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void changeAddress(Address address) throws InvalidStatusException {
        CheckUtils.isTrue(
                serviceStatus.equals(DealerServiceStatusType.IN_SERVICE),
                new InvalidStatusException(
                        serviceStatus.getDescription(),
                        "Dealer 修改地址"
                )
        );

        CheckUtils.notNull(address, "address 必须不为 null");

        this.address = address;

        DomainEventPublisher.instance().publish(new DealerUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void changeTelephone(String telephone) throws InvalidStatusException {
        CheckUtils.isTrue(
                serviceStatus.equals(DealerServiceStatusType.IN_SERVICE),
                new InvalidStatusException(
                        serviceStatus.getDescription(),
                        "Dealer 修改联系电话"
                )
        );

        CheckUtils.notBlank(telephone, "telephone 必须不为空");

        this.telephone = telephone;

        DomainEventPublisher.instance().publish(new DealerUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void makeDealerVisible() {
        if (visibilityStatus.equals(DealerVisibilityStatusType.HIDDEN)) {
            return;
        }

        visibilityStatus = DealerVisibilityStatusType.HIDDEN;

        DomainEventPublisher.instance().publish(new DealerVisibilityChangedToHidden(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public void makeDealerHidden() {
        if (visibilityStatus.equals(DealerVisibilityStatusType.VISIBLE)) {
            return;
        }

        visibilityStatus = DealerVisibilityStatusType.VISIBLE;

        DomainEventPublisher.instance().publish(new DealerVisibilityChangedToVisible(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public DealerServicePurchaseOrder purchaseService(
            DealerServicePurchaseOrderService dealerServicePurchaseOrderService,
            DealerServicePurchaseOrderIdGenerator dealerServicePurchaseOrderIdGenerator) {
        if (dealerServicePurchaseOrderService.existsOrderInProcessing(this)) {
            throw new RuntimeException();
        }

        LocalDateTime servicePeriodBegin;
        if (DealerServiceStatusType.IN_SERVICE.equals(serviceStatus)) {
            servicePeriodBegin = serviceExpiryTime;

        } else {
            servicePeriodBegin = LocalDateTime.now();
        }
        LocalDateTime servicePeriodEnd = servicePeriodBegin.plusYears(1);

        return new DealerServicePurchaseOrder(
                dealerServicePurchaseOrderIdGenerator.nextId(),
                this.id,
                new TimeRange(servicePeriodBegin, servicePeriodEnd)
        );
    }

    public void extendServiceExpiryTime(TimeRange servicePeriod) {

    }

    public void terminateServiceUponExpiration() {

    }

    public DealerId id() {
        return id;
    }

    public boolean isInService() {
        return serviceStatus.equals(DealerServiceStatusType.IN_SERVICE);
    }

    public boolean isVisible() {
        return visibilityStatus.equals(DealerVisibilityStatusType.VISIBLE);
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
