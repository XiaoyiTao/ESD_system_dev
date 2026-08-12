package com.foxconn.iad.esd.platform;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PlatformUserResp {

    /** 平台用户 ID。 */
    private Long id;
    /** 平台账号，作为 ESD 员工工號快照。 */
    private String username;
    /** 平台用户昵称，作为 ESD 员工姓名快照。 */
    private String nickname;
    /** 平台账号状态，0 为启用。 */
    private Integer status;
    /** 平台部门 ID。 */
    private Long deptId;
    /** 平台厂区字段，多个厂区使用逗号分隔。 */
    private String site;

    /** 将平台厂区字符串转换成可用于权限判断的列表。 */
    public List<String> getSiteCodes() {
        if (site == null || site.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] values = site.split(",");
        List<String> result = new java.util.ArrayList<>();
        for (String value : values) {
            if (!value.trim().isEmpty()) {
                result.add(value.trim());
            }
        }
        return result;
    }
}
