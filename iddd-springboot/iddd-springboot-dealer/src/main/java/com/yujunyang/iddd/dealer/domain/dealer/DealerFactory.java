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

import java.text.MessageFormat;

import com.yujunyang.iddd.common.domain.id.IdGenerator;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.IdUtils;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.car.Brand;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.car.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerFactory {
    private IdGenerator idGenerator;
    private DealerNameUniquenessCheckService dealerNameUniquenessCheckService;

    @Autowired
    public DealerFactory(
            IdGenerator idGenerator,
            DealerNameUniquenessCheckService dealerNameUniquenessCheckService) {
        this.idGenerator = idGenerator;
        this.dealerNameUniquenessCheckService = dealerNameUniquenessCheckService;
    }

    public Dealer createDealer(
            String name,
            CityId cityId,
            String specificAddress,
            String telephone,
            BrandId brandId) {
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notNull(cityId, "cityId 必须不为 null");
        CheckUtils.notBlank(specificAddress, "specificAddress 必须不为空");
        CheckUtils.notNull(brandId, "brandId 必须不为 null");
        CheckUtils.notBlank(telephone, "telephone 必须不为空");



        boolean nameNotUsed = dealerNameUniquenessCheckService.isNameNotUsed(name);
        if (!nameNotUsed) {
            throw new IllegalArgumentException(
                    MessageFormat.format("name({0}) 已被使用", name)
            );
        }

        return new Dealer(
                new DealerId(IdUtils.newId()),
                name,
                new Address(
                        cityId,
                        specificAddress
                ),
                telephone,
                brandId);
    }
}
