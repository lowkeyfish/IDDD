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

package com.yujunyang.iddd.dealer.infrastructure.persistence;

import java.util.Optional;

import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.DealerStatusType;
import com.yujunyang.iddd.dealer.infrastructure.persistence.mybatis.mapper.DealerMapper;
import com.yujunyang.iddd.dealer.infrastructure.persistence.mybatis.model.DealerDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisDealerRepository implements DealerRepository {
    private DealerMapper dealerMapper;

    @Autowired
    public MyBatisDealerRepository(
            DealerMapper dealerMapper) {
        this.dealerMapper = dealerMapper;
    }

    @Override
    public Dealer findById(DealerId dealerId) {
        return Optional.ofNullable(dealerMapper.selectById(dealerId.getId()))
                .map(n -> convert(n))
                .orElse(null);
    }

    @Override
    public Dealer findByName(String name) {
        return Optional.ofNullable(dealerMapper.selectByName(name))
                .map(n -> convert(n))
                .orElse(null);
    }

    @Override
    public void save(Dealer dealer) {
        DealerDatabaseModel databaseModel = convert(dealer);
        if (dealerMapper.insertOrIgnore(databaseModel) > 0) {
            return;
        }

        dealerMapper.update(databaseModel);
    }

    private Dealer convert(DealerDatabaseModel databaseModel) {
        return new Dealer(
                new DealerId(databaseModel.getId()),
                databaseModel.getName(),
                new Address(
                        new CityId(databaseModel.getCityId()),
                        databaseModel.getSpecificAddress()
                ),
                databaseModel.getTelephone(),
                new BrandId(databaseModel.getBrandId()),
                databaseModel.getCreateTime(),
                DealerStatusType.parse(databaseModel.getStatus()),
                servicePeriod);
    }

    private DealerDatabaseModel convert(Dealer model) {
        DealerDatabaseModel databaseModel = new DealerDatabaseModel();
        databaseModel.setId(model.getId().getId());
        databaseModel.setName(model.getName());
        databaseModel.setSpecificAddress(model.getAddress().getSpecificAddress());
        databaseModel.setCityId(model.getAddress().getCityId().getId());
        databaseModel.setTelephone(model.getTelephone());
        databaseModel.setBrandId(model.getBrandId().getId());
        databaseModel.setStatus(model.getStatus().getValue());
        databaseModel.setCreateTime(model.getCreateTime());
        return databaseModel;
    }
}
