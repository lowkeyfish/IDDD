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

package com.yujunyang.iddd.car.application.command;

import com.yujunyang.iddd.common.utils.CheckUtils;

public class BrandCreateCommand {
    private String name;
    private String logo;
    private String firstLetter;

    public BrandCreateCommand(
            String name,
            String logo,
            String firstLetter) {
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(logo, "logo 必须不为空");
        CheckUtils.notBlank(firstLetter, "firstLetter 必须不为空");
        this.name = name;
        this.logo = logo;
        this.firstLetter = firstLetter;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getFirstLetter() {
        return firstLetter;
    }
}
