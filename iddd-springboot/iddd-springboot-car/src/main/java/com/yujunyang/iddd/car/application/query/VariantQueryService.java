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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yujunyang.iddd.car.application.query.data.VariantViewModel;
import com.yujunyang.iddd.car.domain.model.ModelId;
import com.yujunyang.iddd.car.domain.variant.VariantId;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.VariantMapper;
import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.model.VariantDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VariantQueryService {
    private VariantMapper variantMapper;

    @Autowired
    public VariantQueryService(
            VariantMapper variantMapper) {
        this.variantMapper = variantMapper;
    }

    public VariantViewModel findById(VariantId id) {
        return Optional.ofNullable(variantMapper.selectById(id.getId())).map(n -> convert(n)).orElse(null);
    }

    public List<VariantViewModel> findByIds(List<VariantId> ids) {
        return variantMapper.selectByIds(ids.stream().map(n -> n.getId()).collect(Collectors.toList()))
                .stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    public List<VariantViewModel> findByModelIds(List<ModelId> modelIds) {
        return variantMapper.selectByModelIds(modelIds.stream().map(n -> n.getId()).collect(Collectors.toList()))
                .stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    private VariantViewModel convert(VariantDatabaseModel databaseModel) {
        VariantViewModel viewModel = new VariantViewModel();
        viewModel.setId(databaseModel.getId());
        viewModel.setName(databaseModel.getName());
        viewModel.setPrice(databaseModel.getPrice());
        viewModel.setModelId(databaseModel.getModelId());
        viewModel.setBrandId(databaseModel.getBrandId());
        viewModel.setManufacturerId(databaseModel.getManufacturerId());
        return viewModel;
    }
}
