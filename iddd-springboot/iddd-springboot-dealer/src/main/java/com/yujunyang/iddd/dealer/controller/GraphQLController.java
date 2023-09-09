package com.yujunyang.iddd.dealer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yujunyang.iddd.dealer.graphql.GraphQLExecutor;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.validation.ValidationError;
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
        try {
            String query = (String) body.get("query");
            Map<String, Object> variables = (Map<String, Object>) body.get("variables");
            if (variables == null) {
                variables = new LinkedHashMap<>();
            }

            ExecutionResult executionResult = graphQLExecutor.execute(query, variables);
            Map<String, Object> ret = new LinkedHashMap<>();
            if (executionResult.getErrors().size() > 0) {
                List<Object> errors = new ArrayList<>();
                executionResult.getErrors().forEach(graphQLError -> {
                    Map<String, Object> specification = graphQLError.toSpecification();
                    LinkedHashMap extensions = (LinkedHashMap) specification.get("extensions");
                    if (graphQLError instanceof ExceptionWhileDataFetching) {
                        Throwable exception = ((ExceptionWhileDataFetching) graphQLError).getException();
                        if (exception instanceof IllegalArgumentException) {
                            specification.put("message", exception.getMessage());
                            extensions.put("code", 400);
                        } else {
                            LOGGER.error(exception.getMessage(), exception);
                            specification.put("message", "服务器内部错误");
                            extensions.put("code", 500);
                        }
                    } else if (graphQLError instanceof ValidationError) {
                        extensions.put("code", -1);
                    }
                    errors.add(specification);
                });
                ret.put("errors", errors);
            }

            ret.put("data", executionResult.getData());

            return ret;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Map<String, Object> ret = new HashMap<>();
            Map<String, Object> error = new HashMap<>();
            error.put("message", "服务器内部错误");
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("code", 500);
            error.put("extensions", extensions);
            ret.put("errors", Arrays.asList(error));
            ret.put("data", null);
            return ret;
        }
    }
}