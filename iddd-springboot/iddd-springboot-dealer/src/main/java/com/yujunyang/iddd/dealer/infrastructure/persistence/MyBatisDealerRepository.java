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

import com.yujunyang.iddd.common.domain.id.IdGenerator;
import com.yujunyang.iddd.dealer.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerIdGenerator;
import com.yujunyang.iddd.dealer.domain.dealer.DealerServiceStatusType;
import com.yujunyang.iddd.dealer.domain.dealer.DealerVisibilityStatusType;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import com.yujunyang.iddd.dealer.domain.dealer.DealerSnapshot;
import com.yujunyang.iddd.dealer.infrastructure.persistence.mybatis.mapper.DealerMapper;
import com.yujunyang.iddd.dealer.infrastructure.persistence.mybatis.model.DealerDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisDealerRepository implements DealerRepository, DealerIdGenerator {
    private IdGenerator idGenerator;
    private DealerMapper dealerMapper;

    @Autowired
    public MyBatisDealerRepository(
            IdGenerator idGenerator,
            DealerMapper dealerMapper) {
        this.idGenerator = idGenerator;
        this.dealerMapper = dealerMapper;
    }

    @Override
    public DealerId nextId() {
        return new DealerId(idGenerator.nextId());
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
                DealerVisibilityStatusType.parse(databaseModel.getVisibleStatus()),
                DealerServiceStatusType.parse(databaseModel.getServiceStatus()),
                databaseModel.getServiceExpiryTime());
    }

    private DealerDatabaseModel convert(Dealer model) {
        DealerSnapshot snapshot = model.snapshot();
        DealerDatabaseModel databaseModel = new DealerDatabaseModel();
        databaseModel.setId(snapshot.getId().getId());
        databaseModel.setName(snapshot.getName());
        databaseModel.setSpecificAddress(snapshot.getAddress().getSpecificAddress());
        databaseModel.setCityId(snapshot.getAddress().getCityId().getId());
        databaseModel.setTelephone(snapshot.getTelephone());
        databaseModel.setBrandId(snapshot.getBrandId().getId());
        databaseModel.setVisibleStatus(snapshot.getVisibilityStatus().getValue());
        databaseModel.setCreateTime(snapshot.getCreateTime());
        databaseModel.setServiceStatus(snapshot.getServiceStatus().getValue());
        databaseModel.setServiceExpiryTime(snapshot.getServiceExpiryTime());
        return databaseModel;
    }
}
