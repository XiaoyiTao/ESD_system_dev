package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.common.PageResult;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetPageReqVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetRespVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetUpdateReqVO;

/**
 * 資產主檔業務服務。
 *
 * <p>所有新資產從可用庫存開始，發放、清洗和終止狀態只能由後續業務交易改變。</p>
 */
public interface AssetService {

    /** 新增一件可用庫存資產。 */
    Long create(AssetCreateReqVO request);

    /** 編輯資產的顏色和尺碼，要求客戶端帶回最新版本號。 */
    void update(Long id, AssetUpdateReqVO request);

    /** 按廠區和篩選條件分頁查詢資產，關鍵字同時搜索編碼和當前持有人快照。 */
    PageResult<AssetRespVO> page(AssetPageReqVO request);

    /** 查詢單件資產詳情，並再次校驗廠區權限。 */
    AssetRespVO get(Long id, String siteCode);
}
