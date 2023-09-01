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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public final class RedisUtils {
    private static final String COMMON_PREFIX = "iddd";

    public static String generateKey(Object... segments) {
        CheckUtils.isTrue(segments.length > 0, "segments 必须不为空");

        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(COMMON_PREFIX)) {
            list.add(COMMON_PREFIX);
        }
        list.addAll(Arrays.stream(segments).map(n -> String.valueOf(n)).collect(Collectors.toList()));
        return String.join(":", list);
    }

}
