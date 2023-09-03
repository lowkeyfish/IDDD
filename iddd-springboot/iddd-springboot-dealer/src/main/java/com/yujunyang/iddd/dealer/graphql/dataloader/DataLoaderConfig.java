package com.yujunyang.iddd.dealer.graphql.dataloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoaderConfig {
    @Autowired
    public DataLoaderConfig(
            ) {
    }

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        return dataLoaderRegistry;
    }

}
