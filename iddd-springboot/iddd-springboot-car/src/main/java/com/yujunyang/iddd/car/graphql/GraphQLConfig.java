package com.yujunyang.iddd.car.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.yujunyang.iddd.car.graphql.dataloader.DataLoaderConfig;
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
import org.dataloader.DataLoader;
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
                    builder.dataFetcher("models", modelsDataFetcher());
                    builder.dataFetcher("node", nodeDataFetcher());
                    return builder;
                })
                .type("Brand", builder -> {
                    builder.dataFetcher("models", findModelsInBrandDataFetcher());
                    builder.dataFetcher("manufacturers", findManufacturersInBrandDataFetcher());
                    return builder;
                })
                .type("Manufacturer", builder -> {
                    builder.dataFetcher("models", findModelsInManufacturerDataFetcher());
                    return builder;
                })
                .type("Model", builder -> {
                    builder.dataFetcher("variants", findVariantsInModelDataFetcher());
                    builder.dataFetcher("brand", findBrandInModelDataFetcher());
                    builder.dataFetcher("manufacturer", findManufacturerInModelDataFetcher());
                    return builder;
                })
                .type("Variant", builder -> {
                    builder.dataFetcher("model", findModelInVariantDataFetcher());
                    builder.dataFetcher("brand", findBrandInVariantDataFetcher());
                    builder.dataFetcher("manufacturer", findManufacturerInVariantDataFetcher());
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

    private DataFetcher<?> findModelsInBrandDataFetcher() {
        return environment -> {
            BrandViewModel brandViewModel = environment.getSource();
            DataLoader<String, List<ModelViewModel>> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_MODELS_BY_BRAND_IDS);
            return dataLoader.load(brandViewModel.getId());
        };
    }

    private DataFetcher<?> findModelsInManufacturerDataFetcher() {
        return environment -> {
            ManufacturerViewModel manufacturerViewModel = environment.getSource();
            DataLoader<String, List<ModelViewModel>> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_MODELS_BY_MANUFACTURER_IDS);
            return dataLoader.load(manufacturerViewModel.getId());
        };
    }

    private DataFetcher<?> findManufacturersInBrandDataFetcher() {
        return environment -> {
            BrandViewModel brandViewModel = environment.getSource();
            DataLoader<String, List<ManufacturerViewModel>> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_MANUFACTURERS_BY_BRAND_IDS);
            return dataLoader.load(brandViewModel.getId());
        };
    }

    private DataFetcher<List<ModelViewModel>> modelsDataFetcher() {
        return environment -> {
            String brandId = environment.getArgument("brandId");
            List<String> modelIds = environment.getArgument("modelIds");

            return modelQueryService.findBy(
                    BrandId.parse(brandId),
                    Optional.ofNullable(modelIds)
                            .orElse(new ArrayList<>()).stream().map(n -> ModelId.parse(n))
                            .collect(Collectors.toList())
            );
        };
    }

    private DataFetcher<?> findVariantsInModelDataFetcher() {
        return environment -> {
            ModelViewModel modelViewModel = environment.getSource();
            DataLoader<String, List<VariantViewModel>> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_VARIANTS_BY_MODEL_IDS);
            return dataLoader.load(modelViewModel.getId());
        };
    }

    private DataFetcher<?> findBrandInModelDataFetcher() {
        return environment -> {
            ModelViewModel modelViewModel = environment.getSource();
            DataLoader<String, BrandViewModel> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_BRAND_BY_IDS);
            return dataLoader.load(modelViewModel.getBrandId());
        };
    }

    private DataFetcher<?> findManufacturerInModelDataFetcher() {
        return environment -> {
            ModelViewModel modelViewModel = environment.getSource();
            DataLoader<String, ManufacturerViewModel> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_MANUFACTURER_BY_IDS);
            return dataLoader.load(modelViewModel.getManufacturerId());
        };
    }

    private DataFetcher<?> findModelInVariantDataFetcher() {
        return environment -> {
            VariantViewModel variantViewModel = environment.getSource();
            DataLoader<String, ModelViewModel> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_MODEL_BY_IDS);
            return dataLoader.load(variantViewModel.getModelId());
        };
    }

    private DataFetcher<?> findBrandInVariantDataFetcher() {
        return environment -> {
            VariantViewModel variantViewModel = environment.getSource();
            DataLoader<String, BrandViewModel> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_BRAND_BY_IDS);
            return dataLoader.load(variantViewModel.getBrandId());
        };
    }

    private DataFetcher<?> findManufacturerInVariantDataFetcher() {
        return environment -> {
            VariantViewModel variantViewModel = environment.getSource();
            DataLoader<String, ManufacturerViewModel> dataLoader = environment.getDataLoader(DataLoaderConfig.DATALOADER_MANUFACTURER_BY_IDS);
            return dataLoader.load(variantViewModel.getManufacturerId());
        };
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

}
