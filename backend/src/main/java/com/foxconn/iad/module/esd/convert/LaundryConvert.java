package com.foxconn.iad.module.esd.convert;

import com.foxconn.iad.module.esd.controller.admin.laundry.vo.LaundryRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.LaundryRecordDO;
import com.foxconn.iad.module.esd.enums.AssetLifecycleStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 清洗記錄 + 資產 → 清洗列表 VO 轉換。
 *
 * <p>類型、顏色、尺碼與當前狀態來自資產，送洗時間與操作人來自清洗記錄。</p>
 */
@Mapper
public interface LaundryConvert {

    LaundryConvert INSTANCE = Mappers.getMapper(LaundryConvert.class);

    /** 組合清洗記錄與資產資訊成 API 響應。 */
    @Mapping(target = "id", source = "record.id")
    @Mapping(target = "assetCode", source = "asset.assetCode")
    @Mapping(target = "assetType", source = "asset.assetType")
    @Mapping(target = "colorCode", source = "asset.colorCode")
    @Mapping(target = "sizeCode", source = "asset.sizeCode")
    @Mapping(target = "lifecycleStatus", source = "asset.lifecycleStatus")
    @Mapping(target = "lifecycleStatusName", source = "asset.lifecycleStatus", qualifiedByName = "toStatusName")
    @Mapping(target = "sendTime", source = "record.sendTime")
    @Mapping(target = "sendOperatorName", source = "record.sendOperatorName")
    LaundryRespVO convert(LaundryRecordDO record, AssetDO asset);

    /** 依生命周期狀態編碼查詢中文名稱。 */
    @Named("toStatusName")
    default String toStatusName(Integer code) {
        return AssetLifecycleStatus.displayNameOf(code);
    }
}
