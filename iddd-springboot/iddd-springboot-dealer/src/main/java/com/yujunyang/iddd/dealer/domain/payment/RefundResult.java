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

import java.util.Map;

public class RefundResult {
    private Map<String, Object> refundDetails;
    private RefundStatusType status;

    public RefundResult(
            Map<String, Object> refundDetails,
            RefundStatusType status) {
        this.refundDetails = refundDetails;
        this.status = status;
    }

    public RefundStatusType status() {
        return status;
    }

    public Map<String, Object> details() {
        return refundDetails;
    }
}
