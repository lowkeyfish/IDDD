package com.yujunyang.iddd.car.graphql.dataloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.yujunyang.iddd.car.application.BrandQueryService;
import com.yujunyang.iddd.car.application.ManufacturerQueryService;
import com.yujunyang.iddd.car.application.ModelQueryService;
import com.yujunyang.iddd.car.application.VariantQueryService;
import com.yujunyang.iddd.car.application.data.BrandViewModel;
import com.yujunyang.iddd.car.application.data.ManufacturerViewModel;
import com.yujunyang.iddd.car.application.data.ModelViewModel;
import com.yujunyang.iddd.car.application.data.VariantViewModel;
import com.yujunyang.iddd.car.domain.brand.BrandId;
import com.yujunyang.iddd.car.domain.manufacturer.ManufacturerId;
import com.yujunyang.iddd.car.domain.model.ModelId;
import org.apache.commons.collections4.CollectionUtils;
import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoaderConfig {

    public static final String DATALOADER_BRAND_BY_IDS = "DATALOADER_BRAND_BY_IDS";
    public static final String DATALOADER_MANUFACTURER_BY_IDS = "DATALOADER_MANUFACTURER_BY_IDS";
    public static final String DATALOADER_MANUFACTURERS_BY_BRAND_IDS = "DATALOADER_MANUFACTURERS_BY_BRAND_IDS";
    public static final String DATALOADER_MODEL_BY_IDS = "DATALOADER_MODEL_BY_IDS";
    public static final String DATALOADER_MODELS_BY_BRAND_IDS = "DATALOADER_MODELS_BY_BRAND_IDS";
    public static final String DATALOADER_MODELS_BY_MANUFACTURER_IDS = "DATALOADER_MODELS_BY_MANUFACTURER_IDS";
    public static final String DATALOADER_VARIANTS_BY_MODEL_IDS = "DATALOADER_VARIANTS_BY_MODEL_IDS";

    private BrandQueryService brandQueryService;
    private ManufacturerQueryService manufacturerQueryService;
    private ModelQueryService modelQueryService;
    private VariantQueryService variantQueryService;

    @Autowired
    public DataLoaderConfig(
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
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register(DATALOADER_BRAND_BY_IDS, DataLoaderFactory.newDataLoader(findBrandByIdsBatchLoader()));
        dataLoaderRegistry.register(DATALOADER_MANUFACTURER_BY_IDS, DataLoaderFactory.newDataLoader(findManufacturerByIdsBatchLoader()));
        dataLoaderRegistry.register(DATALOADER_MANUFACTURERS_BY_BRAND_IDS, DataLoaderFactory.newDataLoader(findManufacturersByBrandIdsBatchLoader()));
        dataLoaderRegistry.register(DATALOADER_MODEL_BY_IDS, DataLoaderFactory.newDataLoader(findModelByIdsBatchLoader()));
        dataLoaderRegistry.register(DATALOADER_MODELS_BY_BRAND_IDS, DataLoaderFactory.newDataLoader(findModelsByBrandIdsBatchLoader()));
        dataLoaderRegistry.register(DATALOADER_MODELS_BY_MANUFACTURER_IDS, DataLoaderFactory.newDataLoader(findModelsByManufacturerIdsBatchLoader()));
        dataLoaderRegistry.register(DATALOADER_VARIANTS_BY_MODEL_IDS, DataLoaderFactory.newDataLoader(findVariantsByModelIdsBatchLoader()));
        return dataLoaderRegistry;
    }

    private BatchLoaderWithContext<Long, BrandViewModel> findBrandByIdsBatchLoader() {
        BatchLoaderWithContext<Long, BrandViewModel> batchLoaderWithContext = (list, batchLoaderEnvironment) -> CompletableFuture.supplyAsync(() -> {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<BrandViewModel> brandViewModelList = brandQueryService.findByIds(
                    list.stream().map(n -> new BrandId(n)).distinct().collect(Collectors.toList()));
            List<BrandViewModel> ret = new ArrayList<>();
            list.forEach(n -> {
                ret.add(brandViewModelList.stream().filter(n2 -> n2.getId() == n).findFirst().orElse(null));
            });
            return ret;
        });
        return batchLoaderWithContext;
    }

    private BatchLoaderWithContext<Long, ManufacturerViewModel> findManufacturerByIdsBatchLoader() {
        BatchLoaderWithContext<Long, ManufacturerViewModel> batchLoaderWithContext = (list, batchLoaderEnvironment) -> CompletableFuture.supplyAsync(() -> {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<ManufacturerViewModel> manufacturerViewModelList = manufacturerQueryService.findByIds(
                    list.stream().map(n -> new ManufacturerId(n)).distinct().collect(Collectors.toList()));
            List<ManufacturerViewModel> ret = new ArrayList<>();
            list.forEach(n -> {
                ret.add(manufacturerViewModelList.stream().filter(n2 -> n2.getId() == n).findFirst().orElse(null));
            });
            return ret;
        });
        return batchLoaderWithContext;
    }

    private BatchLoaderWithContext<Long, List<ManufacturerViewModel>> findManufacturersByBrandIdsBatchLoader() {
        BatchLoaderWithContext<Long, List<ManufacturerViewModel>> batchLoaderWithContext = (list, batchLoaderEnvironment) -> CompletableFuture.supplyAsync(() -> {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<ManufacturerViewModel> manufacturerViewModelList = manufacturerQueryService.findByBrandIds(
                    list.stream().map(n -> new BrandId(n)).distinct().collect(Collectors.toList()));
            List<List<ManufacturerViewModel>> ret = new ArrayList<>();
            list.forEach(n -> {
                ret.add(manufacturerViewModelList.stream().filter(n2 -> n2.getBrandId() == n).collect(Collectors.toList()));
            });
            return ret;
        });
        return batchLoaderWithContext;
    }

    private BatchLoaderWithContext<Long, ModelViewModel> findModelByIdsBatchLoader() {
        BatchLoaderWithContext<Long, ModelViewModel> batchLoaderWithContext = (list, batchLoaderEnvironment) -> CompletableFuture.supplyAsync(() -> {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<ModelViewModel> modelViewModelList = modelQueryService.findByIds(
                    list.stream().map(n -> new ModelId(n)).distinct().collect(Collectors.toList()));
            List<ModelViewModel> ret = new ArrayList<>();
            list.forEach(n -> {
                ret.add(modelViewModelList.stream().filter(n2 -> n2.getId() == n).findFirst().orElse(null));
            });
            return ret;
        });
        return batchLoaderWithContext;
    }

    private BatchLoaderWithContext<Long, List<ModelViewModel>> findModelsByBrandIdsBatchLoader() {
        BatchLoaderWithContext<Long, List<ModelViewModel>> batchLoaderWithContext = (list, batchLoaderEnvironment) -> CompletableFuture.supplyAsync(() -> {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<ModelViewModel> modelViewModelList = modelQueryService.findByBrandIds(
                    list.stream().map(n -> new BrandId(n)).distinct().collect(Collectors.toList()));
            List<List<ModelViewModel>> ret = new ArrayList<>();
            list.forEach(n -> {
                ret.add(modelViewModelList.stream().filter(n2 -> n2.getBrandId() == n).collect(Collectors.toList()));
            });
            return ret;
        });
        return batchLoaderWithContext;
    }

    private BatchLoaderWithContext<Long, List<ModelViewModel>> findModelsByManufacturerIdsBatchLoader() {
        BatchLoaderWithContext<Long, List<ModelViewModel>> batchLoaderWithContext = (list, batchLoaderEnvironment) -> CompletableFuture.supplyAsync(() -> {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<ModelViewModel> modelViewModelList = modelQueryService.findByManufacturerIds(
                    list.stream().map(n -> new ManufacturerId(n)).distinct().collect(Collectors.toList()));
            List<List<ModelViewModel>> ret = new ArrayList<>();
            list.forEach(n -> {
                ret.add(modelViewModelList.stream().filter(n2 -> n2.getManufacturerId() == n).collect(Collectors.toList()));
            });
            return ret;
        });
        return batchLoaderWithContext;
    }

    private BatchLoaderWithContext<Long, List<VariantViewModel>> findVariantsByModelIdsBatchLoader() {
        BatchLoaderWithContext<Long, List<VariantViewModel>> batchLoaderWithContext = (list, batchLoaderEnvironment) -> CompletableFuture.supplyAsync(() -> {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<VariantViewModel> variantViewModelList = variantQueryService.findByModelIds(
                    list.stream().map(n -> new ModelId(n)).distinct().collect(Collectors.toList()));
            List<List<VariantViewModel>> ret = new ArrayList<>();
            list.forEach(n -> {
                ret.add(variantViewModelList.stream().filter(n2 -> n2.getModelId() == n).collect(Collectors.toList()));
            });
            return ret;
        });
        return batchLoaderWithContext;
    }
}
