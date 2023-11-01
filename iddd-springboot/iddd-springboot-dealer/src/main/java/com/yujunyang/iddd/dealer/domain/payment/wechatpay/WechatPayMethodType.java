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

package com.yujunyang.iddd.dealer.domain.payment.wechatpay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yujunyang.iddd.common.enums.ValueDescriptionEnum;
import com.yujunyang.iddd.common.utils.EnumUtilsEnhance;

public enum WechatPayMethodType implements ValueDescriptionEnum<Integer> {
    JSAPI(1, "JSAPI"),
    NATIVE(2, "Native"),
    APP(3, "App"),
    H5(4, "H5");

    @JsonValue
    private int value;
    private String description;

    WechatPayMethodType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WechatPayMethodType parse(Object value) {
        return EnumUtilsEnhance.getByIntValueOrStringName(value, WechatPayMethodType.class);
    }
}
