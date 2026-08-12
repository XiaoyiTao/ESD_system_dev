package com.foxconn.iad.esd.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** 0 表示成功；非 0 值与 HTTP 状态共同表达业务错误。 */
    private int code;
    /** 返回给前端的简短提示。 */
    private String msg;
    /** 成功时的业务数据，失败时为空。 */
    private T data;

    /** 构造成功响应，保持与平台 CommonResult 的 code 约定一致。 */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    /** 构造失败响应，避免控制器重复创建响应对象。 */
    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
