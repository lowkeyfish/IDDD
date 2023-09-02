package com.yujunyang.iddd.car.graphql.dataloader;

import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoaderConfig {

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();

        DataLoaderOptions dataLoaderOptions = new DataLoaderOptions().setCachingEnabled(false);
        return dataLoaderRegistry;
    }


}
