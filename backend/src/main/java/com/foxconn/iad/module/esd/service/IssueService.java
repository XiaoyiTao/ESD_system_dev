package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.common.PageResult;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssuePageReqVO;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueRespVO;

/**
 * 發放業務服務。
 *
 * <p>發放將庫存資產交付給在職員工，並在同一事務內完成條件更新、發放記錄、
 * 資產事件和人員快照。</p>
 */
public interface IssueService {

    /** 單筆發放：庫存 -> 發放中。 */
    Long create(IssueCreateReqVO request);

    /** 按廠區分頁查詢發放記錄，一個資產多次發放全部顯示。 */
    PageResult<IssueRespVO> page(IssuePageReqVO request);
}
