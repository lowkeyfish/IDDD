package com.yujunyang.iddd.dealer.graphql;

import java.util.List;

import com.yujunyang.iddd.common.graphql.directive.DateTimeFormatDirective;
import com.yujunyang.iddd.common.graphql.scalar.CustomScalars;
import com.yujunyang.iddd.dealer.application.query.DealerQueryService;
import com.yujunyang.iddd.dealer.application.query.data.DealerViewModel;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.SelectedField;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {
    private DealerQueryService dealerQueryService;

    @Autowired
    public GraphQLConfig(
            DealerQueryService dealerQueryService) {

        this.dealerQueryService = dealerQueryService;
    }

    @Bean
    public GraphQL graphQL() {
        GraphQL graphQL = GraphQL.newGraphQL(schema()).build();
        return graphQL;
    }

    private GraphQLSchema schema() {
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry(), runtimeWiring());
    }

    private TypeDefinitionRegistry typeDefinitionRegistry() {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        typeDefinitionRegistry.merge(schemaParser.parse(this.getClass().getResourceAsStream("/graphql/schema.graphqls")));
        typeDefinitionRegistry.merge(schemaParser.parse(this.getClass().getResourceAsStream("/graphql/query.graphqls")));
        //typeDefinitionRegistry.merge(schemaParser.parse(this.getClass().getResourceAsStream("/graphql/mutation.graphqls")));
        return typeDefinitionRegistry;
    }

    private RuntimeWiring runtimeWiring() {
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .scalar(CustomScalars.DATE_TIME)
                .directive(DateTimeFormatDirective.DIRECTIVE_NAME, new DateTimeFormatDirective())
                .scalar(ExtendedScalars.GraphQLBigDecimal)
                .scalar(ExtendedScalars.GraphQLLong)
                .type("Query", builder -> {
                    builder.dataFetcher("node", nodeDataFetcher());
                    return builder;
                })
                .type("Node", builder -> {
                    builder.typeResolver(nodeTypeResolver());
                    return builder;
                })
                .build();
        return runtimeWiring;
    }

    private DataFetcher<Object> nodeDataFetcher() {
        return environment -> {
            String type = nodeType(environment);
            if ("Dealer".equalsIgnoreCase(type)) {
                return null;
            }

            return null;
        };
    }

    private String nodeType(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();

        for (SelectedField selectedField : selectionSet.getFields()) {
            List<GraphQLObjectType> objectTypes = selectedField.getObjectTypes();

            if (objectTypes.size() == 1) {
                return objectTypes.get(0).getName();
            }
        }

        return "";
    }

    private TypeResolver nodeTypeResolver() {
        return node -> {
            if (node.getObject() instanceof DealerViewModel) {
                return node.getSchema().getObjectType("Dealer");
            }

            return null;
        };
    }

}
