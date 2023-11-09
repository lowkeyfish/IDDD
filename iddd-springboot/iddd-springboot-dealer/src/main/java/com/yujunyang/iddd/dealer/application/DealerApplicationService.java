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

package com.yujunyang.iddd.dealer.application;

import java.text.MessageFormat;

import com.google.common.collect.ImmutableMap;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.exception.NameNotUniqueException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.application.command.ChangeDealerVisibilityCommand;
import com.yujunyang.iddd.dealer.application.command.DealerChangeAddressCommand;
import com.yujunyang.iddd.dealer.application.command.DealerChangeNameCommand;
import com.yujunyang.iddd.dealer.application.command.DealerChangeTelephoneCommand;
import com.yujunyang.iddd.dealer.application.command.DealerCreateCommand;
import com.yujunyang.iddd.dealer.application.command.UpdateServiceTimeOnServicePurchaseOrderPaymentSuccessCommand;
import com.yujunyang.iddd.dealer.application.data.DealerCreateCommandResult;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.address.City;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.address.CityService;
import com.yujunyang.iddd.dealer.domain.car.Brand;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.car.BrandService;
import com.yujunyang.iddd.dealer.domain.dealer.CityBrandSupportedService;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerIdGenerator;
import com.yujunyang.iddd.dealer.domain.dealer.DealerNameUniquenessCheckService;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrder;
import com.yujunyang.iddd.dealer.domain.dealer.servicepurchase.DealerServicePurchaseOrderRepository;
import com.yujunyang.iddd.dealer.domain.order.OrderStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerApplicationService {
    private DealerNameUniquenessCheckService dealerNameUniquenessCheckService;
    private DealerRepository dealerRepository;
    private DealerIdGenerator dealerIdGenerator;
    private BrandService brandService;
    private CityService cityService;
    private CityBrandSupportedService cityBrandSupportedService;
    private DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository;

    @Autowired
    public DealerApplicationService(
            DealerNameUniquenessCheckService dealerNameUniquenessCheckService,
            DealerRepository dealerRepository,
            DealerIdGenerator dealerIdGenerator,
            BrandService brandService,
            CityService cityService,
            CityBrandSupportedService cityBrandSupportedService,
            DealerServicePurchaseOrderRepository dealerServicePurchaseOrderRepository) {
        this.dealerNameUniquenessCheckService = dealerNameUniquenessCheckService;
        this.dealerRepository = dealerRepository;
        this.dealerIdGenerator = dealerIdGenerator;
        this.brandService = brandService;
        this.cityService = cityService;
        this.cityBrandSupportedService = cityBrandSupportedService;
        this.dealerServicePurchaseOrderRepository = dealerServicePurchaseOrderRepository;
    }

    @Transactional
    public void create(
            DealerCreateCommand command,
            DealerCreateCommandResult commandResult)
            throws NameNotUniqueException {
        CheckUtils.notNull(command, "command 必须不为 null");

        City city = existingCity(command.getCityId());
        Brand brand = existingBrand(command.getBrandId());
        cityBrandSupportedService.check(city, brand);

        CheckUtils.isTrue(
                dealerNameUniquenessCheckService.isNameNotUsed(command.getName()),
                new NameNotUniqueException(command.getName())
        );

        Dealer dealer = new Dealer(
                dealerIdGenerator.nextId(),
                command.getName(),
                new Address(
                        command.getCityId(),
                        command.getSpecificAddress()
                ),
                command.getTelephone(),
                command.getBrandId()
        );

        dealerRepository.save(dealer);

        commandResult.resultingDealerId(dealer.id().getId());
    }

    @Transactional
    public void changeName(DealerChangeNameCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());
        dealer.changeName(command.getName(), dealerNameUniquenessCheckService);

        dealerRepository.save(dealer);
    }

    @Transactional
    public void changeTelephone(DealerChangeTelephoneCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());
        dealer.changeTelephone(command.getTelephone());

        dealerRepository.save(dealer);
    }

    @Transactional
    public void changeAddress(DealerChangeAddressCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());
        dealer.changeAddress(new Address(
                command.getCityId(),
                command.getSpecificAddress()
        ));

        dealerRepository.save(dealer);
    }

    @Transactional
    public void makeDealerHidden(ChangeDealerVisibilityCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());
        dealer.makeDealerHidden();

        dealerRepository.save(dealer);
    }

    @Transactional
    public void makeDealerVisible(ChangeDealerVisibilityCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Dealer dealer = existingDealer(command.getDealerId());
        dealer.makeDealerVisible();

        dealerRepository.save(dealer);
    }

    @Transactional
    public void updateServiceTimeOnServicePurchaseOrderPaymentSuccess(
            UpdateServiceTimeOnServicePurchaseOrderPaymentSuccessCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        DealerServicePurchaseOrder order = dealerServicePurchaseOrderRepository
                .findById(command.getDealerServicePurchaseOrderId());
        CheckUtils.notNull(
                order,
                new BusinessRuleException(
                        "服务购买订单不存在",
                        ImmutableMap.of(
                                "dealerServicePurchaseOrderId",
                                command.getDealerServicePurchaseOrderId().getId()
                        )
                )
        );
        CheckUtils.isTrue(
                OrderStatusType.PAID.equals(order.status()),
                new BusinessRuleException(
                        "服务购买订单未支付成功",
                        ImmutableMap.of(
                                "dealerServicePurchaseOrderId",
                                command.getDealerServicePurchaseOrderId().getId()
                        )
                )
        );

        Dealer dealer = existingDealer(order.dealerId());
        dealer.updateServiceTime(order.serviceExpiryTime());

        dealerRepository.save(dealer);
    }

    private Dealer existingDealer(DealerId dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId);
        if (dealer == null) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Dealer({0}) 不存在", dealerId)
            );
        }
        return dealer;
    }

    private City existingCity(CityId cityId) {
        City city = cityService.findById(cityId);
        CheckUtils.notNull(
                city,
                "cityId({0}) 无效",
                cityId
        );
        return city;
    }

    private Brand existingBrand(BrandId brandId) {
        Brand brand = brandService.findById(brandId);
        CheckUtils.notNull(
                brand,
                "brandId({0}) 无效", brandId
        );
        return brand;
    }
}
