package com.foxconn.iad.module.esd.convert;

import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.ReturnRecordDO;
import com.foxconn.iad.module.esd.enums.ReturnDisposition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 回收記錄 DO → VO 轉換。
 */
@Mapper
public interface ReturnConvert {

    ReturnConvert INSTANCE = Mappers.getMapper(ReturnConvert.class);

    /** 將回收記錄轉換成 API 響應，處置碼轉成中文名稱。 */
    @Mapping(target = "dispositionName", source = "disposition", qualifiedByName = "toDispositionName")
    ReturnRespVO convert(ReturnRecordDO bean);

    /** 依回收處置編碼查詢中文名稱。 */
    @Named("toDispositionName")
    default String toDispositionName(Integer code) {
        return ReturnDisposition.displayNameOf(code);
    }
}
