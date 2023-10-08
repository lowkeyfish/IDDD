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

package com.yujunyang.iddd.car.infrastructure.persistence;

import java.util.Optional;

import com.yujunyang.iddd.car.domain.brand.Brand;
import com.yujunyang.iddd.car.domain.brand.BrandId;
import com.yujunyang.iddd.car.domain.brand.BrandRepository;
import com.yujunyang.iddd.car.domain.brand.BrandSnapshot;
import com.yujunyang.iddd.car.domain.brand.BrandStatusType;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.BrandMapper;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.model.BrandDatabaseModel;
import com.yujunyang.iddd.common.domain.id.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisBrandRepository implements BrandRepository {
    private BrandMapper brandMapper;
    private IdGenerator idGenerator;

    @Autowired
    public MyBatisBrandRepository(
            BrandMapper brandMapper,
            IdGenerator idGenerator) {
        this.brandMapper = brandMapper;
        this.idGenerator = idGenerator;
    }


    @Override
    public BrandId nextId() {
        return new BrandId(idGenerator.nextId());
    }

    @Override
    public Brand findById(BrandId id) {
        BrandDatabaseModel databaseModel = brandMapper.selectById(id.getId());
        return Optional.ofNullable(databaseModel).map(n -> convert(n)).orElse(null);
    }

    @Override
    public Brand findByName(String name) {
        BrandDatabaseModel databaseModel = brandMapper.selectByName(name);
        return Optional.ofNullable(databaseModel).map(n -> convert(n)).orElse(null);
    }

    @Override
    public void save(Brand brand) {
        BrandDatabaseModel databaseModel = convert(brand);
        boolean exits = brandMapper.insert(databaseModel) == 0;
        if (exits) {
            brandMapper.update(databaseModel);
        }
    }

    private BrandDatabaseModel convert(Brand brand) {
        BrandSnapshot snapshot = brand.snapshot();
        BrandDatabaseModel brandDatabaseModel = new BrandDatabaseModel();
        brandDatabaseModel.setId(snapshot.getId().getId());
        brandDatabaseModel.setName(snapshot.getName());
        brandDatabaseModel.setLogo(snapshot.getLogo());
        brandDatabaseModel.setFirstLetter(snapshot.getFirstLetter());
        return brandDatabaseModel;
    }

    private Brand convert(BrandDatabaseModel databaseModel) {
        return new Brand(
                BrandId.parse(databaseModel.getId()),
                databaseModel.getName(),
                databaseModel.getLogo(),
                databaseModel.getFirstLetter(),
                BrandStatusType.DEFAULT
        );
    }
}
