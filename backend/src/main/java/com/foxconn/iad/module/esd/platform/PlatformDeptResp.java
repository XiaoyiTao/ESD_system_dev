package com.foxconn.iad.module.esd.platform;

import lombok.Data;

@Data
public class PlatformDeptResp {

    /** 部門 ID。 */
    private Long id;
    /** 部門名稱。 */
    private String name;
    /** 父部門 ID。 */
    private Long parentId;
    /** 部門負責人用戶 ID。 */
    private Long leaderUserId;
    /** 部門狀態，0 為啟用。 */
    private Integer status;
}
