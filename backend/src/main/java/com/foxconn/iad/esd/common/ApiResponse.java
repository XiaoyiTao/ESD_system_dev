package com.foxconn.iad.esd.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** 0 表示成功；非 0 值與 HTTP 狀態共同表達業務錯誤。 */
    private int code;
    /** 返回給前端的簡短提示。 */
    private String msg;
    /** 成功時的業務數據，失敗時為空。 */
    private T data;

    /** 構造成功響應，保持與平台 CommonResult 的 code 約定一致。 */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    /** 構造失敗響應，避免控制器重複創建響應對象。 */
    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
