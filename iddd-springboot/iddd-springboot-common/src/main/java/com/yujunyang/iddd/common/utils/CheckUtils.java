package com.yujunyang.iddd.common.utils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public final class CheckUtils {
    public static <T> void notNull(T object, String errorMessagePattern, Object... errorMessageArgs) {
        notNull(object, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <T, E extends RuntimeException> void notNull(T object, E exception) throws E {
        if (object == null) {
            throw exception;
        }
    }

    public static <T, E extends Exception> void notNull(T object, E exception) throws E {
        if (object == null) {
            throw exception;
        }
    }

    public static void anyNotNull(Object[] objects, String errorMessagePattern, Object... errorMessageArgs) {
        anyNotNull(objects, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void anyNotNull(Object[] objects, E exception) throws E {
        if (!ObjectUtils.anyNotNull(objects)) {
            throw exception;
        }
    }

    public static <E extends Exception> void anyNotNull(Object[] objects, E exception) throws E {
        if (!ObjectUtils.anyNotNull(objects)) {
            throw exception;
        }
    }

    public static void notBlank(String string, String errorMessagePattern, Object... errorMessageArgs) {
        notBlank(string, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void notBlank(String string, E exception) {
        notNull(string, exception);
        if (StringUtils.isBlank(string)) {
            throw exception;
        }
    }

    public static <E extends Exception> void notBlank(String string, E exception) throws E {
        notNull(string, exception);
        if (StringUtils.isBlank(string)) {
            throw exception;
        }
    }

    public static void anyNotBlank(String[] strings, String errorMessagePattern, Object... errorMessageArgs) {
        anyNotBlank(strings, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void anyNotBlank(String[] strings, E exception) {
        boolean allBlank = true;
        for (String string : strings) {
            if (StringUtils.isNotBlank(string)) {
                allBlank = false;
                break;
            }
        }
        if (allBlank) {
            throw exception;
        }
    }

    public static <E extends Exception> void anyNotBlank(String[] strings, E exception) throws E {
        boolean allBlank = true;
        for (String string : strings) {
            if (StringUtils.isNotBlank(string)) {
                allBlank = false;
                break;
            }
        }
        if (allBlank) {
            throw exception;
        }
    }

    public static void isTrue(boolean expression, String errorMessagePattern, Object... errorMessageArgs) {
        isTrue(expression, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void isTrue(boolean expression, E exception) {
        if (!expression) {
            throw exception;
        }
    }

    public static <E extends Exception> void isTrue(boolean expression, E exception) throws E {
        if (!expression) {
            throw exception;
        }
    }

    public static void moreThan(int number, int compareNumber, String errorMessagePattern, Object... errorMessageArgs) {
        moreThan(number, compareNumber, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void moreThan(int number, int compareNumber, E exception) {
        if (number <= compareNumber) {
            throw exception;
        }
    }

    public static <E extends Exception> void moreThan(int number, int compareNumber, E exception) throws E {
        if (number <= compareNumber) {
            throw exception;
        }
    }

    public static void moreThan(long number, long compareNumber, String errorMessagePattern, Object... errorMessageArgs) {
        moreThan(number, compareNumber, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void moreThan(long number, long compareNumber, E exception) {
        if (number <= compareNumber) {
            throw exception;
        }
    }

    public static <E extends Exception> void moreThan(long number, long compareNumber, E exception) throws E {
        if (number <= compareNumber) {
            throw exception;
        }
    }

    public static void notEmpty(Collection collection, String errorMessagePattern, Object... errorMessageArgs) {
        notEmpty(collection, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void notEmpty(Collection collection, E exception) {
        if (collection == null || collection.isEmpty()) {
            throw exception;
        }
    }

    public static <E extends Exception> void notEmpty(Collection collection, E exception) throws Exception {
        if (collection == null || collection.isEmpty()) {
            throw exception;
        }
    }

    public static void notEmpty(Map map, String errorMessagePattern, Object... errorMessageArgs) {
        notEmpty(map, new IllegalArgumentException(MessageFormat.format(errorMessagePattern, errorMessageArgs)));
    }

    public static <E extends RuntimeException> void notEmpty(Map map, E exception) {
        if (map == null || map.isEmpty()) {
            throw exception;
        }
    }

    public static <E extends Exception> void notEmpty(Map map, E exception) throws Exception {
        if (map == null || map.isEmpty()) {
            throw exception;
        }
    }
}
