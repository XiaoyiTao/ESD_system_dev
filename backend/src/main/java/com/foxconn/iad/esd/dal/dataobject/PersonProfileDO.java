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

    /** ESD 人员档主键，不与平台用户表建立跨库外键。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 厂区编码，用于所有业务数据隔离。 */
    private String siteCode;
    /** 统一平台用户 ID。 */
    private Long platformUserId;
    /** 从平台同步的员工工号快照。 */
    private String employeeNo;
    /** 从平台同步的员工姓名快照。 */
    private String employeeName;
    /** 平台部门 ID。 */
    private Long deptId;
    /** 平台部门名称快照。 */
    private String deptName;
    /** 责任主管的平台用户 ID。 */
    private Long supervisorUserId;
    /** 责任主管姓名快照。 */
    private String supervisorName;
    /** 业务现场属性：楼层。 */
    private String floorCode;
    /** 业务现场属性：班别。 */
    private String shiftCode;
    /** ESD 人员档状态：1 启用，0 停用。 */
    private Integer esdStatus;
    /** 创建操作人 ID。 */
    private String creator;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 最后修改操作人 ID。 */
    private String updater;
    /** 最后修改时间。 */
    private LocalDateTime updateTime;
    /** 逻辑删除标记，由 MyBatis-Plus 自动追加过滤条件。 */
    @TableLogic
    private Boolean deleted;
}
