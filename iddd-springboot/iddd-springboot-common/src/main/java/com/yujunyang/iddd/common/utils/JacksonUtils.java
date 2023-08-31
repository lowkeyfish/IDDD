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

    public static <T> T deSerialize(String jsonString, Class<T> classT) {
        return deSerialize(jsonString, classT, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> T deSerialize(String jsonString, Class<T> classT, ObjectMapper objectMapper) {
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

    public static <T> T deSerialize(String jsonString, TypeReference<T> typeReference) {
        return deSerialize(jsonString, typeReference, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> T deSerialize(String jsonString, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> T deSerialize(String jsonString, JavaType javaType) {
        return deSerialize(jsonString, javaType, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> T deSerialize(String jsonString, JavaType javaType, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> T deSerialize(String string, Type type) {
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        return deSerialize(string, javaType);
    }

    public static <T> List<T> deSerializeList(String jsonString, Class<T> classT) {
        return deSerializeList(jsonString, classT, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> List<T> deSerializeList(String jsonString, Class<T> classT, ObjectMapper objectMapper) {
        JavaType javaType = getCollectionType(objectMapper, ArrayList.class, classT);
        return (List) deSerialize(jsonString, javaType, objectMapper);
    }

    public static JavaType getCollectionType(ObjectMapper objectMapper, Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
