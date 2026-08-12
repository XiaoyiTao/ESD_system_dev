package com.foxconn.iad.esd.platform;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PlatformUserResp {

    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private Long deptId;
    private String site;

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

