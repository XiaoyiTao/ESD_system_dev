package com.foxconn.iad.esd.controller.dto.person;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PersonProfileUpdateReq {

    @NotBlank
    @Size(max = 32)
    private String siteCode;
    private Long supervisorUserId;
    @Size(max = 32)
    private String floorCode;
    @Size(max = 32)
    private String shiftCode;
    @Min(0)
    @Max(1)
    private Integer esdStatus;
}

