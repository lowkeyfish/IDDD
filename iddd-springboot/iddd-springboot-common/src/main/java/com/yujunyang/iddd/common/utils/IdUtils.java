package com.yujunyang.iddd.common.utils;

import java.util.UUID;

public final class IdUtils {
    public static String newId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
