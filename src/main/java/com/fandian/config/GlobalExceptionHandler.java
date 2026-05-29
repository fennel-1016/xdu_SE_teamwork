package com.fandian.config;

import com.fandian.model.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常拦截器
 * 将所有底层异常封装为友好的 JSON 格式返回，避免暴露内部调用栈
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 参数校验失败
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidation(Exception e) {
        log.warn("参数校验失败: {}", e.getMessage());
        return Result.fail(400, "请求参数不合法");
    }

    /**
     * 缺少请求头
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleMissingHeader(MissingRequestHeaderException e) {
        log.warn("缺少请求头: {}", e.getHeaderName());
        return Result.fail(401, "缺少认证信息");
    }

    /**
     * 请求体格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBadRequest(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误");
        return Result.fail(400, "请求数据格式错误");
    }

    /**
     * 数据库异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleDatabase(DataAccessException e) {
        log.error("数据库异常", e);
        return Result.fail(500, "食堂大厨忙碌中，请稍后重试");
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleUnknown(Exception e) {
        log.error("未知异常", e);
        return Result.fail(500, "服务器繁忙，请稍后重试");
    }
}
