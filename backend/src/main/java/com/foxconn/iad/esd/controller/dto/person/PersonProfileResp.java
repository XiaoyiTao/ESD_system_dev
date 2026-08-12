package com.foxconn.iad.esd.controller.dto.person;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonProfileResp {

    private Long id;
    private String siteCode;
    private Long platformUserId;
    private String employeeNo;
    private String employeeName;
    private Long deptId;
    private String deptName;
    private Long supervisorUserId;
    private String supervisorName;
    private String floorCode;
    private String shiftCode;
    private Integer esdStatus;
    private long garmentCount;
    private long shoesCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

