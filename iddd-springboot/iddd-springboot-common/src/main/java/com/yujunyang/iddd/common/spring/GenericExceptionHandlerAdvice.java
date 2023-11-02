/*
 * Copyright 2023 Yu Junyang
 * https://github.com/lowkeyfish
 *
 * This file is part of IDDD.
 *
 * IDDD is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IDDD is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IDDD.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.yujunyang.iddd.common.spring;

import com.yujunyang.iddd.common.data.RestResponse;
import com.yujunyang.iddd.common.exception.BusinessRuleException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericExceptionHandlerAdvice {
    private static final Logger LOGGER = LogManager.getLogger(GenericExceptionHandlerAdvice.class);

    @ExceptionHandler({
            MissingServletRequestParameterException.class
    })
    @ResponseBody
    public RestResponse handleBadRequest(Exception exception) {
        RestResponse responseMessage;
        if (exception instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException parameterException = (MissingServletRequestParameterException) exception;
            String message = String.format("参数'%s'未提供", parameterException.getParameterName());
            responseMessage = new RestResponse(400, message);

        } else {
            responseMessage = new RestResponse(400, exception.getMessage());
        }

        return responseMessage;
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    @ResponseBody
    public RestResponse handleApplicationProactiveException(Exception exception) {
        RestResponse responseMessage;
        if (exception instanceof IllegalArgumentException) {
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException) exception;
            responseMessage = new RestResponse(400, illegalArgumentException.getMessage());
            return responseMessage;
        } else {
            responseMessage = new RestResponse(400, exception.getMessage());
        }

        return responseMessage;

    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public RestResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        RestResponse responseMessage = new RestResponse(405, String.format("当前链接不支持%s方式请求", exception.getMethod()));
        return responseMessage;
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    @ResponseBody
    public RestResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        RestResponse responseMessage = new RestResponse(400, "未提供请求参数");
        return responseMessage;
    }

    @ExceptionHandler
    @ResponseBody
    public RestResponse handleOtherException(Exception exception) {
        LOGGER.error(exception.getMessage(), exception);
        RestResponse responseMessage = new RestResponse(500, "服务器内部错误");
        return responseMessage;
    }

    @ExceptionHandler({
            BusinessRuleException.class
    })
    @ResponseBody
    public RestResponse handleBusinessRuleException(BusinessRuleException exception) {
        RestResponse responseMessage = new RestResponse(exception.getCode(), exception.getMessage());
        return responseMessage;

    }
}

