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

package com.yujunyang.iddd.common.utils;

import java.util.function.Supplier;

public final class FlowUtils {
    public static void ifThenElse(Supplier<Boolean> predicate, Runnable thenRunnable, Runnable elseRunnable) {
        if (predicate.get()) {
            thenRunnable.run();
        } else {
            elseRunnable.run();
        }
    }

    public static void ifThen(Supplier<Boolean> predicate, Runnable thenRunnable) {
        if (predicate.get()) {
            thenRunnable.run();
        }
    }

    public static <T> T ifThenElse(Supplier<Boolean> predicate, Supplier<T> thenSupplier, Supplier<T> elseSupplier) {
        if (predicate.get()) {
            return thenSupplier.get();
        } else {
            return elseSupplier.get();
        }
    }


}

