package com.foxconn.iad.module.esd.controller.admin.laundry.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LaundryRespVO {

    /** 清洗記錄 ID。 */
    private Long id;
    /** 物品編碼快照。 */
    private String assetCode;
    /** 資產類型：1 靜電衣，2 靜電鞋。 */
    private Integer assetType;
    /** 顏色編碼。 */
    private String colorCode;
    /** 尺碼編碼。 */
    private String sizeCode;
    /** 資產生命周期狀態編碼：25 待送洗，30 清洗中。 */
    private Integer lifecycleStatus;
    /** 資產生命周期狀態中文名稱。 */
    private String lifecycleStatusName;
    /** 送洗時間。 */
    private LocalDateTime sendTime;
    /** 送洗操作人姓名快照。 */
    private String sendOperatorName;
}
