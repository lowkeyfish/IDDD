package com.yujunyang.iddd.common.graphql.scalar;

import graphql.schema.GraphQLScalarType;

public final class CustomScalars {
    public static final GraphQLScalarType DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("日期时间")
            .coercing(new DateTimeCoercing())
            .build();

}
