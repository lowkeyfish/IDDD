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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.RedisUtils;
import com.yujunyang.iddd.dealer.application.data.DealerViewModel;
import com.yujunyang.iddd.dealer.domain.address.Address;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.car.BrandId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerServiceStatusType;
import com.yujunyang.iddd.dealer.domain.dealer.DealerVisibilityStatusType;
import com.yujunyang.iddd.dealer.infrastructure.persistence.mybatis.mapper.DealerMapper;
import com.yujunyang.iddd.dealer.infrastructure.persistence.mybatis.model.DealerDatabaseModel;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerQueryService {
    private DealerMapper dealerMapper;
    private RedissonClient redissonClient;

    @Autowired
    public DealerQueryService(
            DealerMapper dealerMapper,
            RedissonClient redissonClient) {
        this.dealerMapper = dealerMapper;
        this.redissonClient = redissonClient;
    }

    public DealerViewModel findDealerById(DealerId id) {
        CheckUtils.notNull(id, "id 必须不为 null");

        RBucket<DealerViewModel> bucket = redissonClient.getBucket(redisKeyOfDealer(id));
        DealerViewModel ret = bucket.get();
        if (ret == null) {
            ret = dealerFreshGetAndRefreshCache(id);
        }

        return ret;
    }

    public void refreshDealerCache(DealerId id) {
        dealerFreshGetAndRefreshCache(id);
    }

    private DealerViewModel dealerFreshGetAndRefreshCache(DealerId id) {
        DealerDatabaseModel databaseModel = dealerMapper.selectById(id.getId());
        DealerViewModel ret = Optional.ofNullable(databaseModel).map(n -> convert(n)).orElse(null);
        if (ret != null) {
            RBucket<DealerViewModel> bucket = redissonClient.getBucket(redisKeyOfDealer(id));
            bucket.set(ret, 7, TimeUnit.DAYS);
        }
        return ret;
    }

    private DealerViewModel convert(DealerDatabaseModel databaseModel) {
        DealerViewModel ret = new DealerViewModel();

        ret.setId(new DealerId(databaseModel.getId()));
        ret.setName(databaseModel.getName());
        ret.setAddress(new Address(
                new CityId(databaseModel.getCityId()),
                databaseModel.getSpecificAddress()
        ));
        ret.setTelephone(databaseModel.getTelephone());
        ret.setBrandId(new BrandId(databaseModel.getBrandId()));
        ret.setCreateTime(databaseModel.getCreateTime());
        ret.setServiceStatus(DealerServiceStatusType.parse(databaseModel.getServiceStatus()));
        ret.setVisibilityStatus(DealerVisibilityStatusType.parse(databaseModel.getVisibilityStatus()));
        ret.setServiceExpiryTime(databaseModel.getServiceExpiryTime());

        return ret;
    }

    private String redisKeyOfDealer(DealerId id) {
        return RedisUtils.generateKey(
                "dealer_DealerViewModel",
                id
        );
    }

}
