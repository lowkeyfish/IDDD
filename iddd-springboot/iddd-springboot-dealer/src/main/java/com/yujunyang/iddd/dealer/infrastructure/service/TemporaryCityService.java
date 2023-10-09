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

package com.yujunyang.iddd.dealer.infrastructure.service;

import java.util.Arrays;
import java.util.List;

import com.yujunyang.iddd.dealer.domain.address.City;
import com.yujunyang.iddd.dealer.domain.address.CityId;
import com.yujunyang.iddd.dealer.domain.address.CityService;
import com.yujunyang.iddd.dealer.domain.address.ProvinceId;
import org.springframework.stereotype.Service;

@Service
public class TemporaryCityService implements CityService {
    private static final List<City> LIST = Arrays.asList(
            new City(new CityId(110100), "北京", new ProvinceId(110000))
    );

    @Override
    public City findById(CityId id) {
        return LIST.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public City findByName(String name) {
        return LIST.stream().filter(n -> n.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
