package com.foxconn.iad.module.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("esd_person_profile")
public class PersonProfileDO {

    /** ESD 人員檔主鍵，不與平台用戶表建立跨庫外鍵。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 廠區編碼，用於所有業務數據隔離。 */
    private String siteCode;
    /** 統一平台用戶 ID。 */
    private Long platformUserId;
    /** 從平台同步的員工工號快照。 */
    private String employeeNo;
    /** 從平台同步的員工姓名快照。 */
    private String employeeName;
    /** 平台部門 ID。 */
    private Long deptId;
    /** 平台部門名稱快照。 */
    private String deptName;
    /** 責任主管的平台用戶 ID。 */
    private Long supervisorUserId;
    /** 責任主管姓名快照。 */
    private String supervisorName;
    /** 業務現場屬性：樓層。 */
    private String floorCode;
    /** 業務現場屬性：班別。 */
    private String shiftCode;
    /** ESD 人員檔狀態：1 啟用，0 停用。 */
    private Integer esdStatus;
    /** 創建操作人 ID。 */
    private String creator;
    /** 創建時間。 */
    private LocalDateTime createTime;
    /** 最後修改操作人 ID。 */
    private String updater;
    /** 最後修改時間。 */
    private LocalDateTime updateTime;
    /** 邏輯刪除標記，由 MyBatis-Plus 自動追加過濾條件。 */
    @TableLogic
    private Boolean deleted;
}
