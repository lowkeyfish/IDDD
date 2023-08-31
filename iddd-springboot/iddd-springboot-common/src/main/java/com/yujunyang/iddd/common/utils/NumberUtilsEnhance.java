package com.yujunyang.iddd.common.utils;

import java.math.BigDecimal;

public final class NumberUtilsEnhance {
    private NumberUtilsEnhance() {}

    public static boolean check(String text, int limitScale) {
        try {
            BigDecimal bigDecimal = new BigDecimal(text);
            int scale = bigDecimal.scale();
            return scale <= limitScale;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
