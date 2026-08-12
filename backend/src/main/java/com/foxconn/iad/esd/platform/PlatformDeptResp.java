package com.foxconn.iad.esd.platform;

import lombok.Data;

@Data
public class PlatformDeptResp {

    private Long id;
    private String name;
    private Long parentId;
    private Long leaderUserId;
    private Integer status;
}
