package com.foxconn.iad.esd.platform;

import lombok.Data;

@Data
public class PlatformDeptResp {

    /** 部门 ID。 */
    private Long id;
    /** 部门名称。 */
    private String name;
    /** 父部门 ID。 */
    private Long parentId;
    /** 部门负责人用户 ID。 */
    private Long leaderUserId;
    /** 部门状态，0 为启用。 */
    private Integer status;
}
