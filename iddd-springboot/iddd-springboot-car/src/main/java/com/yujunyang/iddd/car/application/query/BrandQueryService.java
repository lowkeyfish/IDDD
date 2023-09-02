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

package com.yujunyang.iddd.car.application.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yujunyang.iddd.car.application.query.data.BrandViewModel;
import com.yujunyang.iddd.car.domain.brand.BrandId;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.BrandMapper;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.model.BrandDatabaseModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandQueryService {
    private BrandMapper brandMapper;

    @Autowired
    public BrandQueryService(
            BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    public List<BrandViewModel> allBrand() {
        return brandMapper.selectAll()
                .stream()
                .map(n -> convert(n))
                .collect(Collectors.toList());
    }

    public List<BrandViewModel> findBrandByIds(List<BrandId> brandIds) {
        if (CollectionUtils.isEmpty(brandIds)) {
            return new ArrayList<>();
        }
        return brandMapper.selectByIds(brandIds.stream().map(n -> n.getId()).collect(Collectors.toList()))
                .stream()
                .map(n -> convert(n))
                .collect(Collectors.toList());
    }

    public BrandViewModel findById(BrandId brandId) {
        return Optional.ofNullable(brandMapper.selectById(brandId.getId())).map(n -> convert(n)).orElse(null);
    }

    private BrandViewModel convert(BrandDatabaseModel databaseModel) {
        BrandViewModel viewModel = new BrandViewModel();
        viewModel.setId(databaseModel.getId());
        viewModel.setName(databaseModel.getName());
        viewModel.setFirstLetter(databaseModel.getFirstLetter());
        viewModel.setLogo(databaseModel.getLogo());
        return viewModel;
    }
}
