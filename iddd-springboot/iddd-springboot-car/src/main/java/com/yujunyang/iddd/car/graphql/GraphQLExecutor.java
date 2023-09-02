package com.yujunyang.iddd.car.graphql;

import java.util.Map;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphQLExecutor {
    private GraphQL graphQL;
    private DataLoaderRegistry dataLoaderRegistry;

    @Autowired
    public GraphQLExecutor(
            GraphQL graphQL,
            DataLoaderRegistry dataLoaderRegistry) {
        this.graphQL = graphQL;
        this.dataLoaderRegistry = dataLoaderRegistry;
    }

    public ExecutionResult execute(String query, Map<String, Object> variables) {
        ExecutionInput input = ExecutionInput.newExecutionInput(query)
                .variables(variables)
                .dataLoaderRegistry(dataLoaderRegistry)
                .build();

        ExecutionResult executionResult = graphQL.execute(input);
        return executionResult;
    }


}
