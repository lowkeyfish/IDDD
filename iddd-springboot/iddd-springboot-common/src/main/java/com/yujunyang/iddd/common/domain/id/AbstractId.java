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

package com.yujunyang.iddd.common.domain.id;

import com.fasterxml.jackson.annotation.JsonValue;
import com.yujunyang.iddd.common.utils.CheckUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

abstract class AbstractId<T> {
    @JsonValue
    private T id;

    protected AbstractId(T id) {
        CheckUtils.notNull(id, "id必须不为null");
        this.id = id;
    }

    public T getId() {
        return id;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean ret = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            AbstractId typedObject = (AbstractId) anObject;
            ret = this.getId().equals(typedObject.getId());
        }

        return ret;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(initialNonZeroOddNumber(), multiplierNonZeroOddNumber()).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    protected abstract int initialNonZeroOddNumber();

    protected abstract int multiplierNonZeroOddNumber();
}
