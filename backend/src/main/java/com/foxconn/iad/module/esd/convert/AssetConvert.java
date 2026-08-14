package com.foxconn.iad.module.esd.convert;

import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.enums.AssetLifecycleStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 資產主檔 DO → VO 轉換。
 *
 * <p>最近一次發放與清洗快照由業務層另行查詢並寫入，此處忽略。</p>
 */
@Mapper
public interface AssetConvert {

    AssetConvert INSTANCE = Mappers.getMapper(AssetConvert.class);

    /** 將資產資料對象轉換成 API 響應，狀態碼轉成中文名稱。 */
    @Mapping(target = "lifecycleStatusName", source = "lifecycleStatus", qualifiedByName = "toStatusName")
    @Mapping(target = "latestIssue", ignore = true)
    @Mapping(target = "latestLaundry", ignore = true)
    AssetRespVO convert(AssetDO bean);

    /** 依生命周期狀態編碼查詢中文名稱。 */
    @Named("toStatusName")
    default String toStatusName(Integer code) {
        return AssetLifecycleStatus.displayNameOf(code);
    }
}
