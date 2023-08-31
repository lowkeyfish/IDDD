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
