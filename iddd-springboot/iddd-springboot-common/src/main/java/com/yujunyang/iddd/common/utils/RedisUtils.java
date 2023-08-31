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
