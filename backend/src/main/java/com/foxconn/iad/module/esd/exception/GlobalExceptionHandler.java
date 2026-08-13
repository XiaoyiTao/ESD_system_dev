package com.foxconn.iad.module.esd.exception;

import com.foxconn.iad.module.esd.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 將服務端主動拋出的業務異常轉換為統一響應。 */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.failure(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(Exception exception) {
        return ApiResponse.failure(400, "請求參數不合法");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleUnexpectedException(Exception exception) {
        log.error("Unexpected ESD request failure", exception);
        return ApiResponse.failure(500, "系統處理失敗，請稍後重試");
    }
}
    /** 將 Bean Validation 的字段錯誤隱藏為統一的參數錯誤提示。 */
    /** 記錄未預期異常的完整堆棧，但只向客戶端返回通用錯誤。 */
