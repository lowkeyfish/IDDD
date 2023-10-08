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

import com.yujunyang.iddd.car.infrastructure.persistence.mybatis.model.ModelDatabaseModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ModelMapper {
    int insert(ModelDatabaseModel model);

    ModelDatabaseModel selectById(@Param("id") long id);

    int update(ModelDatabaseModel model);

    List<ModelDatabaseModel> selectByBrandId(@Param("brandId") long brandId);

    List<ModelDatabaseModel> selectByIds(@Param("ids") List<Long> ids);

    List<ModelDatabaseModel> selectByBrandIds(@Param("brandIds") List<Long> brandIds);

    List<ModelDatabaseModel> selectByManufacturerIds(@Param("manufacturerIds") List<Long> manufacturerIds);
}
