package com.foxconn.iad.module.esd.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {

    /** 當前頁數據。 */
    private List<T> list;
    /** 按過濾條件計算出的總記錄數。 */
    private long total;
}
