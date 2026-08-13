package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfilePageReqVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileRespVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileUpdateReqVO;

/**
 * ESD 人員擴展檔業務服務。
 *
 * <p>平台是員工主數據唯一來源，ESD 只保存綁定時的工號、姓名和部門快照，
 * 不保存密碼，也不在本地複製平台用戶表。</p>
 */
public interface PersonProfileService {

    /** 綁定平台用戶為 ESD 業務人員。 */
    Long create(PersonProfileCreateReqVO request);

    /** 更新人員業務擴展屬性；平台帳號、工號和姓名不能由本地頁面修改。 */
    void update(Long id, PersonProfileUpdateReqVO request);

    /** 按廠區、部門、啟停狀態和關鍵字分頁查詢。 */
    PageResponse<PersonProfileRespVO> page(PersonProfilePageReqVO request);

    /** 查詢指定廠區的人員詳情。 */
    PersonProfileRespVO get(Long id, String siteCode);
}
