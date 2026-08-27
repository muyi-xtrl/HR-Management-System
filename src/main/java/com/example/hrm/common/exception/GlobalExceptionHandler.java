package com.example.hrm.common.exception;

import com.example.hrm.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + "：" + error.getDefaultMessage())
                .collect(Collectors.joining("；"));
        log.warn("参数校验失败：{}", message);
        return Result.error(400, message);
    }

    /**
     * 约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("；"));
        log.warn("约束校验失败：{}", message);
        return Result.error(400, message);
    }

    /**
     * 数据重复异常（如：工号重复、用户名重复）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("数据重复：{}", e.getMessage());
        return Result.error(409, "数据已存在，请勿重复添加");
    }

    /**
     * SQL 完整性约束异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<String> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.warn("数据库完整性约束冲突：{}", e.getMessage());
        if (e.getMessage().contains("Duplicate entry")) {
            return Result.error(409, "数据已存在，请勿重复添加");
        }
        if (e.getMessage().contains("foreign key")) {
            return Result.error(409, "数据被其他表引用，无法删除");
        }
        return Result.error(409, "数据库操作失败：" + e.getMessage());
    }

    /**
     * 请求参数缺失
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("请求参数缺失：{}", e.getMessage());
        return Result.error(400, "缺少必要参数：" + e.getParameterName());
    }

    /**
     * 自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
    }

    /**
     * 其他所有异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}