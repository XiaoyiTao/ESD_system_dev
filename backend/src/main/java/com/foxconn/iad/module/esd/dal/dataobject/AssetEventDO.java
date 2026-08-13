package com.foxconn.iad.module.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("esd_asset_event")
public class AssetEventDO {

    /** 事件帳主鍵。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 廠區編碼。 */
    private String siteCode;
    /** 資產 ID。 */
    private Long assetId;
    /** 資產編碼快照。 */
    private String assetCode;
    /** 事件類型，如 ISSUE、RETURN、LAUNDER_START、LAUNDER_COMPLETE。 */
    private String eventType;
    /** 流轉前狀態編碼，可空。 */
    private Integer beforeStatus;
    /** 流轉後狀態編碼。 */
    private Integer afterStatus;
    /** 觸發事件的業務記錄 ID，可空。 */
    private Long businessRecordId;
    /** 操作人平台用戶 ID。 */
    private Long operatorUserId;
    /** 操作人姓名快照。 */
    private String operatorName;
    /** 觸發事件的請求 ID。 */
    private String requestId;
    /** 事件附加內容，用於追溯。 */
    private String payloadJson;
    /** 事件發生時間。 */
    private LocalDateTime eventTime;
}
