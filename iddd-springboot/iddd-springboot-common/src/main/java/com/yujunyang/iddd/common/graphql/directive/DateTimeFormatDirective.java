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

package com.yujunyang.iddd.common.graphql.directive;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

import static graphql.Scalars.GraphQLString;

public class DateTimeFormatDirective implements SchemaDirectiveWiring {
    public static final String DIRECTIVE_NAME = "dateTimeFormat";
    private static final String ARGUMENT_NAME = "format";

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();

        DataFetcher originalDateFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);
        DataFetcher formatDateFetcher = DataFetcherFactories.wrapDataFetcher(
                originalDateFetcher,
                (dataFetchingEnvironment, value) -> {
                    String pattern = dataFetchingEnvironment.getArgument(ARGUMENT_NAME);
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                    if (value instanceof LocalDateTime) {
                        return dateTimeFormatter.format((LocalDateTime) value);
                    }
                    return value;
                }
        );
        environment.getCodeRegistry().dataFetcher(parentType, field, formatDateFetcher);
        return field.transform(builder -> builder
                .argument(GraphQLArgument
                        .newArgument()
                        .name(ARGUMENT_NAME)
                        .type(GraphQLString)
                        .defaultValueProgrammatic("yyyy-MM-dd HH:mm:ss")));
    }
}
