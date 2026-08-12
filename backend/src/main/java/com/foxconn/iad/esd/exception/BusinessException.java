package com.foxconn.iad.esd.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    /** 对外返回的业务错误码，例如 403 权限不足、409 版本冲突。 */
    private final int code;

    /** 创建可被全局异常处理器转换为统一响应的业务异常。 */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
