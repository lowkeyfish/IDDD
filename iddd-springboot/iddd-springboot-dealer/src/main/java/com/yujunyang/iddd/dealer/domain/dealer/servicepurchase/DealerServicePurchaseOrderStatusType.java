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

package com.yujunyang.iddd.dealer.domain.dealer.servicepurchase;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yujunyang.iddd.common.enums.ValueDescriptionEnum;
import com.yujunyang.iddd.common.utils.EnumUtilsEnhance;

public enum DealerServicePurchaseOrderStatusType implements ValueDescriptionEnum<Integer> {
    PAYMENT_NOT_INITIATED(1, "未发起支付"),
    PAYMENT_INITIATED(2, "已发起支付"),
    PAYMENT_SUCCESS(3, "支付成功"),
    PAYMENT_FAILURE(4, "支付失败"),
    MANUALLY_CANCELLED(11, "手动取消"),
    TIMEOUT_CANCELLED(12, "超时取消"),
    REFUND_INITIATED(21, "已发起退款"),
    REFUND_SUCCESS(22, "退款成功"),
    REFUND_FAILURE(23, "退款失败");


    @JsonValue
    private int value;
    private String description;

    DealerServicePurchaseOrderStatusType(int value, String description) {
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
    public static DealerServicePurchaseOrderStatusType parse(Object value) {
        return EnumUtilsEnhance.getByIntValueOrStringName(value, DealerServicePurchaseOrderStatusType.class);
    }
}
