package com.yujunyang.iddd.car.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.yujunyang.iddd.car.graphql.GraphQLExecutor;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQLError;
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

        Map<String, Object> ret = new LinkedHashMap<>();

        ExecutionResult executionResult = graphQLExecutor.execute(query, variables);

        if (executionResult.getErrors().size() > 0) {
            GraphQLError graphQLError = executionResult.getErrors().get(0);
            if (graphQLError instanceof ExceptionWhileDataFetching) {
                Throwable exception = ((ExceptionWhileDataFetching) graphQLError).getException();
                if (exception instanceof IllegalArgumentException) {
                    ret.put("code", 400);
                    ret.put("msg", exception.getMessage());
                } else {
                    LOGGER.error("graphQL 执行错误: {}", exception.getMessage(), exception);
                    ret.put("code", 500);
                    ret.put("msg", "服务器内部错误");
                }
            } else {
                ret.put("code", 400);
                ret.put("msg", graphQLError.getMessage());
            }

            ret.put("data", null);
            return ret;
        }
        ret.put("code", 0);
        ret.put("msg", "");
        ret.put("data", executionResult.getData());
        return ret;
    }
}