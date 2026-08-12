package com.foxconn.iad.esd.controller.dto.person;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PersonProfileUpdateReq {

    /** 用于确认记录所属厂区并执行数据隔离。 */
    @NotBlank
    @Size(max = 32)
    private String siteCode;
    /** 可选责任主管，必须来自同一厂区且处于启用状态。 */
    private Long supervisorUserId;
    /** 现场楼层属性。 */
    @Size(max = 32)
    private String floorCode;
    /** 现场班别属性。 */
    @Size(max = 32)
    private String shiftCode;
    /** 1 启用，0 停用；停用前必须确认没有持有资产。 */
    @Min(0)
    @Max(1)
    private Integer esdStatus;
}
