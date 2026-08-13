package com.foxconn.iad.esd.controller.dto.common;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PageQuery {

    /** 頁碼從 1 開始，避免前端傳入 0 導致資料庫分頁語義不一致。 */
    @Min(1)
    private long pageNo = 1;

    /** 單頁最多 200 條，限制一次請求的資料庫和網絡負載。 */
    @Min(1)
    @Max(200)
    private long pageSize = 20;
}
