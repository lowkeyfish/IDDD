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
