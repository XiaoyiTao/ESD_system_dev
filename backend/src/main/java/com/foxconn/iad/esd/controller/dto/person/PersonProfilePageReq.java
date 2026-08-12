package com.foxconn.iad.esd.controller.dto.person;

import com.foxconn.iad.esd.controller.dto.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonProfilePageReq extends PageQuery {

    @NotBlank
    private String siteCode;
    private String keyword;
    private Long deptId;
    private Integer esdStatus;
}

