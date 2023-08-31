/*
 * Copyright 2023 Yu Junyang
 * https://github.com/lowkeyfish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

