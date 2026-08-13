package com.foxconn.iad.esd.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    /** 對外返回的業務錯誤碼，例如 403 權限不足、409 版本衝突。 */
    private final int code;

    /** 創建可被全局異常處理器轉換為統一響應的業務異常。 */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
