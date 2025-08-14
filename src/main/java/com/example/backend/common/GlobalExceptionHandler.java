package com.example.backend.common;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(ErrorCode.VALIDATION_ERROR.code(), message);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(ErrorCode.VALIDATION_ERROR.code(), message);
    }
    
    /**
     * 处理JSON解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleJsonParseException(HttpMessageNotReadableException e) {
        String message = "请求参数格式错误";
        if (e.getCause() instanceof JsonParseException) {
            message = "JSON格式错误：数字不能有前导零，请检查数字字段格式";
        }
        return Result.error(ErrorCode.JSON_ERROR.code(), message);
    }

    /**
     * 处理数据访问/SQL异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleDataAccessException(DataAccessException e) {
        return Result.error(ErrorCode.SQL_ERROR.code(), "数据库访问异常");
    }

    /**
     * 限流抛出的 429
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Result<Void> handleResponseStatusException(ResponseStatusException e) {
        if (e.getStatus() == HttpStatus.TOO_MANY_REQUESTS) {
            return Result.error(ErrorCode.RATE_LIMITED.code(), "请求过于频繁，请稍后再试");
        }
        return Result.error(e.getStatus().value(), e.getReason());
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(ErrorCode.BUSINESS_ERROR.code(), e.getMessage());
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(ErrorCode.SERVER_ERROR.code(), "系统内部错误");
    }
} 
 