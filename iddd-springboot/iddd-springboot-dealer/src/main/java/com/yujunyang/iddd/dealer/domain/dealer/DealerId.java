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

package com.yujunyang.iddd.dealer.domain.dealer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yujunyang.iddd.common.domain.id.AbstractStringId;
import org.apache.commons.lang3.StringUtils;

public class DealerId extends AbstractStringId {
    public DealerId(@JsonProperty("id") String id) {
        super(id);
    }

    @Override
    protected int initialNonZeroOddNumber() {
        return 5;
    }

    @Override
    protected int multiplierNonZeroOddNumber() {
        return 17;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DealerId parse(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return new DealerId(id);
    }
}
