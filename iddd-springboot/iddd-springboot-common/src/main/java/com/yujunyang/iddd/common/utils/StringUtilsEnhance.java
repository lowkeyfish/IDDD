package com.yujunyang.iddd.common.utils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class StringUtilsEnhance {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+\\.?\\d*", Pattern.MULTILINE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[0-9]\\d{9}$");

    public static String extractNumber(String string) {
        return Optional.ofNullable(string).map(n -> {
            final Matcher matcher = NUMBER_PATTERN.matcher(n);
            if (matcher.find()) {
                return matcher.group(0);
            }
            return null;
        }).orElse(null);
    }

    public static boolean isPhone(String text) {
        return StringUtils.isNotBlank(text) && PHONE_PATTERN.matcher(text).matches();
    }

    public static String abbreviate(String text, int maxWidth) {
        return StringUtils.abbreviate(text, "", maxWidth);
    }
}
