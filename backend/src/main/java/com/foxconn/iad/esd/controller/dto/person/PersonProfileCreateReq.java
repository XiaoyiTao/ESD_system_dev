package com.foxconn.iad.esd.controller.dto.person;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PersonProfileCreateReq {

    @NotBlank
    @Size(max = 32)
    private String siteCode;

    @NotNull
    private Long platformUserId;

    private Long supervisorUserId;

    @Size(max = 32)
    private String floorCode;

    @Size(max = 32)
    private String shiftCode;
}

