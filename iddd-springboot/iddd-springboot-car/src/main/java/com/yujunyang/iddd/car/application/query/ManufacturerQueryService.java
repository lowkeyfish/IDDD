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
import java.util.stream.Collectors;

import com.yujunyang.iddd.car.application.query.data.ManufacturerViewModel;
import com.yujunyang.iddd.car.domain.brand.BrandId;
import com.yujunyang.iddd.car.domain.manufacturer.ManufacturerId;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.ManufacturerMapper;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.model.ManufacturerDatabaseModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManufacturerQueryService {
    private ManufacturerMapper manufacturerMapper;

    @Autowired
    public ManufacturerQueryService(
            ManufacturerMapper manufacturerMapper) {
        this.manufacturerMapper = manufacturerMapper;
    }

    public List<ManufacturerViewModel> findByBrandId(BrandId brandId) {
        return manufacturerMapper.selectByBrandId(brandId.getId())
                .stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    public List<ManufacturerViewModel> findByIds(List<ManufacturerId> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return manufacturerMapper.selectByIds(
                ids.stream().map(n -> n.getId()).collect(Collectors.toList())
        ).stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    public List<ManufacturerViewModel> findById(ManufacturerId id) {

    }

    private ManufacturerViewModel convert(ManufacturerDatabaseModel databaseModel) {
        ManufacturerViewModel viewModel = new ManufacturerViewModel();
        viewModel.setId(databaseModel.getId());
        viewModel.setName(databaseModel.getName());
        viewModel.setBrandId(databaseModel.getBrandId());
        return viewModel;
    }
}
