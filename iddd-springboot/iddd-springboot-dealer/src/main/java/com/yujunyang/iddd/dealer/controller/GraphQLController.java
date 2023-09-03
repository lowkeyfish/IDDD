package com.yujunyang.iddd.dealer.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.yujunyang.iddd.dealer.graphql.GraphQLExecutor;
import graphql.ExecutionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GraphQLController {
    private static final Logger LOGGER = LogManager.getLogger(GraphQLController.class);

    private GraphQLExecutor graphQLExecutor;

    @Autowired
    public GraphQLController(
            GraphQLExecutor graphQLExecutor) {
        this.graphQLExecutor = graphQLExecutor;
    }

    @PostMapping("graphql")
    public Object graphql(@RequestBody Map body) {
        String query = (String) body.get("query");
        Map<String, Object> variables = (Map<String, Object>) body.get("variables");
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }

        ExecutionResult executionResult = graphQLExecutor.execute(query, variables);
        return executionResult;
    }
}