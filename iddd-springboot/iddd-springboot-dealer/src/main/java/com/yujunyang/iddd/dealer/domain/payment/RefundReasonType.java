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

package com.yujunyang.iddd.dealer.domain.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yujunyang.iddd.common.enums.ValueDescriptionEnum;
import com.yujunyang.iddd.common.utils.EnumUtilsEnhance;

public enum RefundReasonType implements ValueDescriptionEnum<Integer> {
    PAYMENT_REPEATED(1, "重复支付"),
    USER_REQUEST(2, "用户发起");

    @JsonValue
    private int value;
    private String description;

    RefundReasonType(int value, String description) {
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
    public static RefundReasonType parse(Object value) {
        return EnumUtilsEnhance.getByIntValueOrStringName(value, RefundReasonType.class);
    }
}
