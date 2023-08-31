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

