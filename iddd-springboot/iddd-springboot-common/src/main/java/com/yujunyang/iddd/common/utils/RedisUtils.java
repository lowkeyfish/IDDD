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
