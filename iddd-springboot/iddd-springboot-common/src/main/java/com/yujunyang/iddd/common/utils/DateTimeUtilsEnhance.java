package com.yujunyang.iddd.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public final class DateTimeUtilsEnhance {
    public static long epochSecond() {
        return OffsetDateTime.now().toEpochSecond();
    }

    public static long epochMilliSecond() {
        return OffsetDateTime.now().toInstant().toEpochMilli();
    }

    public static LocalDateTime convert(long epochMilliSecond) {
        return Instant.ofEpochMilli(epochMilliSecond).atOffset(OffsetDateTime.now().getOffset()).toLocalDateTime();
    }

    public static long convert(LocalDateTime localDateTime) {
        return localDateTime.atOffset(OffsetDateTime.now().getOffset()).toInstant().toEpochMilli();
    }
}
