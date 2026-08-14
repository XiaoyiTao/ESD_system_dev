package com.foxconn.iad.module.esd.convert;

import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.PersonProfileDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 人員擴展檔 DO → VO 轉換。
 *
 * <p>衣鞋持有數量為業務統計結果，由業務層查詢後寫入，此處忽略。</p>
 */
@Mapper
public interface PersonProfileConvert {

    PersonProfileConvert INSTANCE = Mappers.getMapper(PersonProfileConvert.class);

    /** 將人員資料對象轉換成 API 響應。 */
    @Mapping(target = "garmentCount", ignore = true)
    @Mapping(target = "shoesCount", ignore = true)
    PersonProfileRespVO convert(PersonProfileDO bean);
}
