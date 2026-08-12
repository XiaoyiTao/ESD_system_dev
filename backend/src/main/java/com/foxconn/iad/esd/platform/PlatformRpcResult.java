package com.foxconn.iad.esd.platform;

import lombok.Data;

@Data
public class PlatformRpcResult<T> {

    private Integer code;
    private String msg;
    private T data;
}

