package com.foxconn.iad.esd.controller.dto.person;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonProfileResp {

    /** ESD 人员档主键。 */
    private Long id;
    /** 当前厂区。 */
    private String siteCode;
    /** 平台用户 ID。 */
    private Long platformUserId;
    /** 员工工號和姓名快照。 */
    private String employeeNo;
    /** 员工姓名快照。 */
    private String employeeName;
    /** 平台部门 ID。 */
    private Long deptId;
    /** 平台部门名称快照。 */
    private String deptName;
    /** 责任主管平台用户 ID。 */
    private Long supervisorUserId;
    /** 责任主管姓名快照。 */
    private String supervisorName;
    /** 现场楼层属性。 */
    private String floorCode;
    /** 现场班别属性。 */
    private String shiftCode;
    /** 1 启用，0 停用。 */
    private Integer esdStatus;
    /** 当前持有的静电衣数量。 */
    private long garmentCount;
    /** 当前持有的静电鞋数量。 */
    private long shoesCount;
    /** 档案创建时间。 */
    private LocalDateTime createTime;
    /** 档案最后修改时间。 */
    private LocalDateTime updateTime;
}
