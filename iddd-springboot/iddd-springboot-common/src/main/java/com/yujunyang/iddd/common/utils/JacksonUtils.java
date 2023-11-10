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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JacksonUtils {
    public static final ObjectMapper ACTIVATE_DEFAULT_TYPING_OBJECT_MAPPER;

    public static final ObjectMapper DEFAULT_OBJECT_MAPPER;

    static {
        ACTIVATE_DEFAULT_TYPING_OBJECT_MAPPER = activateDefaultTypingObjectMapper();
        DEFAULT_OBJECT_MAPPER = defaultObjectMapper();
    }

    private static ObjectMapper activateDefaultTypingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static <T> String serialize(T object) {
        return serialize(object, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> String serialize(T object, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T deserialize(String jsonString, Class<T> classT) {
        return deserialize(jsonString, classT, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> T deserialize(String jsonString, Class<T> classT, ObjectMapper objectMapper) {
        if (jsonString != null && !jsonString.equalsIgnoreCase("")) {
            try {
                return objectMapper.readValue(jsonString, classT);
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }
        } else {
            return null;
        }
    }

    public static <T> T deserialize(String jsonString, TypeReference<T> typeReference) {
        return deserialize(jsonString, typeReference, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> T deserialize(String jsonString, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> T deserialize(String jsonString, JavaType javaType) {
        return deserialize(jsonString, javaType, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> T deserialize(String jsonString, JavaType javaType, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> T deserialize(String string, Type type) {
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        return deserialize(string, javaType);
    }

    public static <T> List<T> deserializeList(String jsonString, Class<T> classT) {
        return deserializeList(jsonString, classT, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> List<T> deserializeList(String jsonString, Class<T> classT, ObjectMapper objectMapper) {
        JavaType javaType = getCollectionType(objectMapper, ArrayList.class, classT);
        return (List) deserialize(jsonString, javaType, objectMapper);
    }

    public static JavaType getCollectionType(ObjectMapper objectMapper, Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
