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

package com.yujunyang.iddd.car.domain.brand;

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;

public class Brand {
    private BrandId id;
    private String name;
    private String logo;
    private String firstLetter;
    private BrandStatusType status;

    public Brand(
            BrandId id,
            String name,
            String logo,
            String firstLetter,
            BrandStatusType status) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.firstLetter = firstLetter;
        this.status = status;
    }

    public Brand(
            BrandId id,
            String name,
            String logo,
            String firstLetter) {
        this(id, name, logo, firstLetter, BrandStatusType.DEFAULT);

        CheckUtils.notNull(id, "id 必须不为 null");
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(logo, "logo 必须不为空");
        CheckUtils.notBlank(firstLetter, "firstLetter 必须不为空");

        DomainEventPublisher.instance().publish(new BrandCreated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                id.getId()
        ));
    }

    public BrandId id() {
        return id;
    }

    public BrandSnapshot snapshot() {
        return new BrandSnapshot(
                id,
                name,
                logo,
                firstLetter
        );
    }
}
