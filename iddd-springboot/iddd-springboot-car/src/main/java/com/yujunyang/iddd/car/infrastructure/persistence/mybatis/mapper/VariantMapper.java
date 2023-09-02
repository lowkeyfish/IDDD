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

package com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper;

import java.util.List;
import java.util.Optional;

import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.mapper.model.VariantDatabaseModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VariantMapper {
    int insert(VariantDatabaseModel model);

    int update(VariantDatabaseModel model);

    VariantDatabaseModel selectById(@Param("id") String id);

    List<VariantDatabaseModel> selectByModelId(@Param("modelId") String modelId);

    List<VariantDatabaseModel> selectByIds(@Param("ids") List<String> ids);

    List<VariantDatabaseModel> selectByModelIds(@Param("modelIds") List<String> modelIds);
}
