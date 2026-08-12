package com.foxconn.iad.esd.controller.dto.common;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PageQuery {

    @Min(1)
    private long pageNo = 1;

    @Min(1)
    @Max(200)
    private long pageSize = 20;
}

