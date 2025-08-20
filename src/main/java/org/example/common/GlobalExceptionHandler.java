package org.example.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<String> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return ApiResponse.fail("运行时错误：" + e.getMessage());
    }

    /**
     * 处理 SQL 唯一约束异常（如重复用户名/邮箱）
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ApiResponse<String> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        return ApiResponse.fail("数据库操作失败：" + e.getMessage());
    }

    /**
     * 处理参数校验异常（如果使用 @Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.fail("参数错误：" + errorMsg);
    }

    /**
     * 捕获所有未处理的异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.fail("系统错误，请联系管理员：" + e.getMessage());
    }
}


