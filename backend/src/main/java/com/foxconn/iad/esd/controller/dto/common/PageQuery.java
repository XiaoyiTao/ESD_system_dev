package com.foxconn.iad.esd.controller.dto.common;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PageQuery {

    /** 页码从 1 开始，避免前端传入 0 导致数据库分页语义不一致。 */
    @Min(1)
    private long pageNo = 1;

    /** 单页最多 200 条，限制一次请求的数据库和网络负载。 */
    @Min(1)
    @Max(200)
    private long pageSize = 20;
}
