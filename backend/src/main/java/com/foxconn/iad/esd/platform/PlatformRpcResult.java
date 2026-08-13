package com.foxconn.iad.esd.platform;

import lombok.Data;

@Data
public class PlatformRpcResult<T> {

    /** 平台 CommonResult code，0 表示成功。 */
    private Integer code;
    /** 平台錯誤消息。 */
    private String msg;
    /** 平台 RPC 返回數據。 */
    private T data;
}
