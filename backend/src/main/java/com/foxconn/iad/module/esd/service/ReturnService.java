package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnPageReqVO;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnRespVO;

/**
 * 回收業務服務。
 *
 * <p>回收將發放中的資產結算：直接入庫回到庫存，或送洗進入待送洗。
 * 回收在同一事務內完成條件更新、回收記錄、發放記錄結算和清洗記錄。</p>
 */
public interface ReturnService {

    /** 單筆回收：發放中 -> 庫存 或 發放中 -> 待送洗。 */
    Long create(ReturnCreateReqVO request);

    /** 按廠區分頁查詢回收記錄。 */
    PageResponse<ReturnRespVO> page(ReturnPageReqVO request);
}
