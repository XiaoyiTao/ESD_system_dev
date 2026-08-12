package com.foxconn.iad.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("esd_person_profile")
public class PersonProfileDO {

    @TableId(type = IdType.ASSIGN_ID)
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
    private String creator;
    private LocalDateTime createTime;
    private String updater;
    private LocalDateTime updateTime;
    @TableLogic
    private Boolean deleted;
}

