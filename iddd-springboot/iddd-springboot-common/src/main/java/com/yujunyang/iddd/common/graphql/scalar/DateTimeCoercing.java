package com.yujunyang.iddd.common.graphql.scalar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class DateTimeCoercing implements Coercing<LocalDateTime, String> {
    @Override
    public String serialize(
            Object dataFetcherResult,
            GraphQLContext graphQLContext,
            Locale locale) throws CoercingSerializeException {
        return (String) dataFetcherResult;
    }

    @Override
    public LocalDateTime parseValue(
            Object input,
            GraphQLContext graphQLContext,
            Locale locale) throws CoercingParseValueException {
        return (LocalDateTime) input;
    }

    @Override
    public LocalDateTime parseLiteral(
            Value<?> input,
            CoercedVariables variables,
            GraphQLContext graphQLContext,
            Locale locale) throws CoercingParseLiteralException {
        if (input instanceof StringValue) {
            return LocalDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return null;
    }
}
