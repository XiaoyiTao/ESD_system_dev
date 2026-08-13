package com.foxconn.iad.module.esd.controller.admin.returns.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReturnRespVO {

    /** 回收記錄 ID。 */
    private Long id;
    /** 物品編碼快照。 */
    private String assetCode;
    /** 資產類型：1 靜電衣，2 靜電鞋。 */
    private Integer assetType;
    /** 歸還員工工號快照。 */
    private String employeeNo;
    /** 歸還員工姓名快照。 */
    private String employeeName;
    /** 歸還人姓名快照。 */
    private String returnerName;
    /** 回收操作人姓名快照。 */
    private String receiverName;
    /** 回收日期。 */
    private LocalDate returnDate;
    /** 後續處置編碼：1 直接入庫，2 送洗。 */
    private Integer disposition;
    /** 後續處置中文名稱。 */
    private String dispositionName;
}
