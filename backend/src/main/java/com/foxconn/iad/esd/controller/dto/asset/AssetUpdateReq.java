package com.foxconn.iad.esd.controller.dto.asset;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AssetUpdateReq {

    /** 资产所属厂区，同时作为更新 SQL 的数据隔离条件。 */
    @NotBlank
    @Size(max = 32)
    private String siteCode;
    @NotBlank
    @Size(max = 32)
    private String colorCode;
    @NotBlank
    @Size(max = 32)
    private String sizeCode;

    @NotNull
    private Integer version;
}
    /** 可编辑颜色。资产类型和编码入库后不可在此接口修改。 */
    /** 可编辑尺码。 */
    /** 客户端读取资产时得到的版本号，服务端使用 CAS 防止覆盖更新。 */
