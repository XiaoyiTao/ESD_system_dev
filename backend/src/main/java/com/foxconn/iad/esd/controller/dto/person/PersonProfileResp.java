package com.foxconn.iad.esd.controller.dto.person;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonProfileResp {

    /** ESD 人員檔主鍵。 */
    private Long id;
    /** 當前廠區。 */
    private String siteCode;
    /** 平台用戶 ID。 */
    private Long platformUserId;
    /** 員工工號和姓名快照。 */
    private String employeeNo;
    /** 員工姓名快照。 */
    private String employeeName;
    /** 平台部門 ID。 */
    private Long deptId;
    /** 平台部門名稱快照。 */
    private String deptName;
    /** 責任主管平台用戶 ID。 */
    private Long supervisorUserId;
    /** 責任主管姓名快照。 */
    private String supervisorName;
    /** 現場樓層屬性。 */
    private String floorCode;
    /** 現場班別屬性。 */
    private String shiftCode;
    /** 1 啟用，0 停用。 */
    private Integer esdStatus;
    /** 當前持有的靜電衣數量。 */
    private long garmentCount;
    /** 當前持有的靜電鞋數量。 */
    private long shoesCount;
    /** 檔案創建時間。 */
    private LocalDateTime createTime;
    /** 檔案最後修改時間。 */
    private LocalDateTime updateTime;
}
