package com.yujunyang.iddd.cardealer.infrastructure.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuthenticationContextDataInjectAspect {
    private LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private ExpressionParser expressionParser = new SpelExpressionParser();
    private AuthenticationContext authenticationContext;

    public AuthenticationContextDataInjectAspect() {
        authenticationContext = new AuthenticationContext();
        User user = new User();
        user.setUserId(123);
        authenticationContext.setUser(user);
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Controller)")
    public void controllerMethod() {}

    @Pointcut("controllerMethod() && (" +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
            ")")
    public void action() {}

    @Pointcut("action() && " +
            "execution(* *(.., @com.yujunyang.iddd.cardealer.infrastructure.aop.AuthenticationContextDataInject (*), ..))")
    public void inject() {}

    @Around("inject()")
    public Object inject(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            AuthenticationContextDataInject annotation = parameter.getAnnotation(AuthenticationContextDataInject.class);
            if (annotation != null) {
                String spel = annotation.value();
                Class<?> parameterType = parameter.getType();
                Expression expression = expressionParser.parseExpression(spel);
                Object value = expression.getValue(authenticationContext, parameterType);
                args[i] = value;
            }
        }

        return joinPoint.proceed(args);
    }


    public static class AuthenticationContext {
        private User user;
        private User userNull;

        public User getUserNull() {
            return userNull;
        }

        public void setUserNull(User userNull) {
            this.userNull = userNull;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public static class User {
        private int userId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
