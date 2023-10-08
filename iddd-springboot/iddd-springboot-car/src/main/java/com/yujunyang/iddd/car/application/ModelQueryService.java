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

package com.yujunyang.iddd.car.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yujunyang.iddd.car.application.data.ModelViewModel;
import com.yujunyang.iddd.car.domain.brand.BrandId;
import com.yujunyang.iddd.car.domain.manufacturer.ManufacturerId;
import com.yujunyang.iddd.car.domain.model.ModelId;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.ModelMapper;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.model.ModelDatabaseModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelQueryService {
    private ModelMapper modelMapper;

    @Autowired
    public ModelQueryService(
            ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ModelViewModel findById(ModelId modelId) {
        return Optional.ofNullable(modelMapper.selectById(modelId.getId())).map(n -> convert(n)).orElse(null);
    }

    public List<ModelViewModel> findByIds(List<ModelId> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return modelMapper.selectByIds(ids.stream().map(n -> n.getId()).collect(Collectors.toList()))
                .stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    public List<ModelViewModel> findByBrandIds(List<BrandId> brandIds) {
        if (CollectionUtils.isEmpty(brandIds)) {
            return new ArrayList<>();
        }
        return modelMapper.selectByBrandIds(brandIds.stream().map(n -> n.getId()).collect(Collectors.toList()))
                .stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    public List<ModelViewModel> findByManufacturerIds(List<ManufacturerId> manufacturerIds) {
        if (CollectionUtils.isEmpty(manufacturerIds)) {
            return new ArrayList<>();
        }
        return modelMapper.selectByManufacturerIds(manufacturerIds.stream().map(n -> n.getId()).collect(Collectors.toList()))
                .stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    public List<ModelViewModel> findBy(BrandId brandId, List<ModelId> modelIds) {
        if (brandId != null) {
            List<ModelViewModel> list = findByBrandIds(Arrays.asList(brandId));
            if (!CollectionUtils.isEmpty(modelIds)) {
                return list.stream().filter(n -> modelIds.contains(ModelId.parse(n.getId())))
                        .collect(Collectors.toList());
            }
            return list;
        }
        return findByIds(modelIds);
    }

    private ModelViewModel convert(ModelDatabaseModel databaseModel) {
        ModelViewModel viewModel = new ModelViewModel();
        viewModel.setId(databaseModel.getId());
        viewModel.setName(databaseModel.getName());
        viewModel.setImage(databaseModel.getImage());
        viewModel.setPriceMin(databaseModel.getPriceMin());
        viewModel.setPriceMax(databaseModel.getPriceMax());
        viewModel.setBrandId(databaseModel.getBrandId());
        viewModel.setManufacturerId(databaseModel.getManufacturerId());
        return viewModel;
    }
}
