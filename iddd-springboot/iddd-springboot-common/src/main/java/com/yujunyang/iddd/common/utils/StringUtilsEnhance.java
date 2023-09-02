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
