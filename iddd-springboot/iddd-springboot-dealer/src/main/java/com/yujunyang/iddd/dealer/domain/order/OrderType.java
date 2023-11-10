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

package com.yujunyang.iddd.dealer.domain.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yujunyang.iddd.common.enums.ValueDescriptionEnum;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.EnumUtilsEnhance;

public enum OrderType implements ValueDescriptionEnum<Integer> {
    DEALER_SERVICE_PURCHASE_ORDER(1, "Dealer 服务购买订单");

    @JsonValue
    private int value;
    private String description;

    OrderType(int value, String description) {
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

    public String routingKeySegment() {

        String segment;
        switch (this) {
            case DEALER_SERVICE_PURCHASE_ORDER:
                segment = "DealerServicePurchaseOrder";
                break;
            default:
                segment = null;
        }

        CheckUtils.notNull(segment, "orderType({0})未支持", this);

        return segment;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OrderType parse(Object value) {
        return EnumUtilsEnhance.getByIntValueOrStringName(value, OrderType.class);
    }
}
