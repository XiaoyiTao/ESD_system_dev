package com.foxconn.iad.module.esd.convert;

import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.IssueRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 發放記錄 DO → VO 轉換。
 */
@Mapper
public interface IssueConvert {

    IssueConvert INSTANCE = Mappers.getMapper(IssueConvert.class);

    /** 將發放記錄轉換成 API 響應。 */
    IssueRespVO convert(IssueRecordDO bean);
}
