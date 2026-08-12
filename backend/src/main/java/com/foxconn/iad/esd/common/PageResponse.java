package com.foxconn.iad.esd.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {

    /** 当前页数据。 */
    private List<T> list;
    /** 按过滤条件计算出的总记录数。 */
    private long total;
}
