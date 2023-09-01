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

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.primitives.Ints;
import com.yujunyang.iddd.common.enums.DescriptionEnum;
import com.yujunyang.iddd.common.enums.ValueEnum;

/**
 * 枚举类型帮助类
 */
public final class EnumUtilsEnhance {
    /**
     * 将枚举类型的可选项转换为map
     * @param eClass 枚举类型
     * @param <E> 枚举类型泛型
     * @return 由枚举可选项的value，description组成的map
     */
    public static <E extends Enum & ValueEnum & DescriptionEnum> Map<String, String> toMap(Class<E> eClass) {
        Map<String, String> map = new LinkedHashMap<>();
        for (E e : eClass.getEnumConstants()) {
            map.put(String.valueOf(e.getValue()), e.getDescription());
        }
        return map;
    }

    /**
     * 将枚举类型的可选项转换为map
     * @param eClass 枚举类型
     * @param exclude 排除掉的枚举值
     * @param <E> 枚举类型泛型
     * @return 由枚举可选项的value，description组成的map
     */
    public static <E extends Enum & ValueEnum & DescriptionEnum> Map<String, String> toMap(Class<E> eClass, E exclude) {
        Map<String, String> map = new LinkedHashMap<>();
        for (E e : eClass.getEnumConstants()) {
            if (e != exclude) {
                map.put(String.valueOf(e.getValue()), e.getDescription());
            }
        }
        return map;
    }

    /**
     * 根据枚举value获取枚举
     * @param value 枚举value
     * @param eClass 枚举类型
     * @param <E> 枚举类型泛型
     * @return 枚举
     */
    public static <V, E extends Enum & ValueEnum<V>> E getByValue(V value, Class<E> eClass) {
        for (E e : eClass.getEnumConstants()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    public static <V, E extends Enum & ValueEnum<V>> E getByValue(V value, Class<E> eClass, E defaultValue) {
        E ret = getByValue(value, eClass);
        return ret == null ? defaultValue : ret;
    }

    public static <E extends Enum & ValueEnum> E getByName(String name, Class<E> eClass) {
        for (E e : eClass.getEnumConstants()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum & ValueEnum> E getByName(String name, Class<E> eClass, E defaultValue) {
        E ret = getByName(name, eClass);
        return ret == null ? defaultValue : ret;
    }

    public static <E extends Enum & ValueEnum<Integer>> E getByIntValueOrStringName(Object value, Class<E> eClass) {
        if (value instanceof Integer) {
            return getByValue((int) value, eClass);
        } else if (value instanceof String) {
            Integer intValue = Ints.tryParse((String) value);
            if (intValue != null) {
                return getByValue((int) value, eClass);
            }
            return getByName((String) value, eClass);
        }
        return null;
    }
}