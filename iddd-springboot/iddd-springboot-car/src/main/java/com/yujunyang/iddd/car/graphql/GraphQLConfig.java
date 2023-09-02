package com.yujunyang.iddd.car.graphql;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yujunyang.iddd.car.application.query.BrandQueryService;
import com.yujunyang.iddd.car.application.query.ManufacturerQueryService;
import com.yujunyang.iddd.car.application.query.ModelQueryService;
import com.yujunyang.iddd.car.application.query.VariantQueryService;
import com.yujunyang.iddd.car.application.query.data.BrandViewModel;
import com.yujunyang.iddd.car.application.query.data.ManufacturerViewModel;
import com.yujunyang.iddd.car.application.query.data.ModelViewModel;
import com.yujunyang.iddd.car.application.query.data.VariantViewModel;
import com.yujunyang.iddd.car.domain.brand.BrandId;
import com.yujunyang.iddd.car.domain.manufacturer.ManufacturerId;
import com.yujunyang.iddd.car.domain.model.ModelId;
import com.yujunyang.iddd.car.domain.variant.VariantId;
import com.yujunyang.iddd.common.graphql.directive.DateTimeFormatDirective;
import com.yujunyang.iddd.common.graphql.scalar.CustomScalars;
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
    private BrandQueryService brandQueryService;
    private ManufacturerQueryService manufacturerQueryService;
    private ModelQueryService modelQueryService;
    private VariantQueryService variantQueryService;

    @Autowired
    public GraphQLConfig(
            BrandQueryService brandQueryService,
            ManufacturerQueryService manufacturerQueryService,
            ModelQueryService modelQueryService,
            VariantQueryService variantQueryService) {
        this.brandQueryService = brandQueryService;
        this.manufacturerQueryService = manufacturerQueryService;
        this.modelQueryService = modelQueryService;
        this.variantQueryService = variantQueryService;
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
                    builder.dataFetcher("brands", brandsDataFetcher());
                    builder.dataFetcher("node", nodeDataFetcher());
                    return builder;
                })
                .type("Node", builder -> {
                    builder.typeResolver(nodeTypeResolver());
                    return builder;
                })
                .type("Mutation", builder -> {
                    return builder;
                })
                .build();
        return runtimeWiring;
    }

    private DataFetcher<Object> nodeDataFetcher() {
        return environment -> {
            String type = nodeType(environment);
            if ("Brand".equalsIgnoreCase(type)) {
                BrandId id = new BrandId(environment.getArgument("id"));
                return brandQueryService.findById(id);
            }
            if ("Manufacturer".equalsIgnoreCase(type)) {
                ManufacturerId id = new ManufacturerId(environment.getArgument("id"));
                return manufacturerQueryService.findById(id);
            }
            if ("Model".equalsIgnoreCase(type)) {
                ModelId id = new ModelId(environment.getArgument("id"));
                return modelQueryService.findById(id);
            }
            if ("Variant".equalsIgnoreCase(type)) {
                VariantId id = new VariantId(environment.getArgument("id"));
                return variantQueryService.findById(id);
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

    private DataFetcher<List<BrandViewModel>> brandsDataFetcher() {
        return environment -> brandQueryService.allBrand();
    }

    private TypeResolver nodeTypeResolver() {
        return node -> {
            if (node.getObject() instanceof BrandViewModel) {
                return node.getSchema().getObjectType("Brand");
            }
            if (node.getObject() instanceof ManufacturerViewModel) {
                return node.getSchema().getObjectType("Manufacturer");
            }
            if (node.getObject() instanceof ModelViewModel) {
                return node.getSchema().getObjectType("Model");
            }
            if (node.getObject() instanceof VariantViewModel) {
                return node.getSchema().getObjectType("Variant");
            }
            return null;
        };
    }

    private DataFetcher createAppraisalTemplateDataFetcher() {
        return environment -> {
            LinkedHashMap input = environment.getArgument("input");
            input.put("enterpriseId", environment.getGraphQlContext().get(GraphQLContextProvider.LOGIN_ENTERPRISE_ID));
            input.put("operatorEmployeeId", environment.getGraphQlContext().get(GraphQLContextProvider.LOGIN_EMPLOYEE_ID));
            CreateAppraisalTemplateCommand command = JacksonUtils.DEFAULT_OBJECT_MAPPER.convertValue(
                    input, CreateAppraisalTemplateCommand.class);
            AppraisalTemplateId appraisalTemplateId = appraisalTemplateApplicationService.createAppraisalTemplate(command);
            Map<String, String> result = new HashMap<>();
            result.put("clientMutationId", String.valueOf(input.get("clientMutationId")));
            result.put("appraisalTemplateId", appraisalTemplateId.toString());
            return result;
        };
    }

    private DataFetcher updateAppraisalTemplateDataFetcher() {
        return environment -> {
            LinkedHashMap input = environment.getArgument("input");
            input.put("id", environment.getArgument("id"));
            input.put("enterpriseId", environment.getGraphQlContext().get(GraphQLContextProvider.LOGIN_ENTERPRISE_ID));
            input.put("operatorEmployeeId", environment.getGraphQlContext().get(GraphQLContextProvider.LOGIN_EMPLOYEE_ID));
            UpdateAppraisalTemplateCommand command = JacksonUtils.DEFAULT_OBJECT_MAPPER.convertValue(
                    input, UpdateAppraisalTemplateCommand.class);
            appraisalTemplateApplicationService.updateAppraisalTemplate(command);
            Map<String, String> result = new HashMap<>();
            result.put("clientMutationId", String.valueOf(input.get("clientMutationId")));
            result.put("appraisalTemplateId", environment.getArgument("id"));
            return result;
        };
    }

}
