package com.foxconn.iad.esd.platform;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PlatformUserResp {

    /** 平台用戶 ID。 */
    private Long id;
    /** 平台帳號，作為 ESD 員工工號快照。 */
    private String username;
    /** 平台用戶暱稱，作為 ESD 員工姓名快照。 */
    private String nickname;
    /** 平台帳號狀態，0 為啟用。 */
    private Integer status;
    /** 平台部門 ID。 */
    private Long deptId;
    /** 平台廠區字段，多個廠區使用逗號分隔。 */
    private String site;

    /** 將平台廠區字符串轉換成可用於權限判斷的列表。 */
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
