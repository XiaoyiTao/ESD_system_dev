package com.foxconn.iad.module.esd.security;

/**
 * 廠區訪問權限校驗服務。
 *
 * <p>所有業務服務必須先調用 {@link #requireSite(String)} 校驗請求廠區，
 * 不能僅相信前端傳入的 siteCode，服務端必須以統一登錄上下文為準。</p>
 */
public interface SiteAccessService {

    /** 校驗請求廠區屬於當前登錄用戶。 */
    LoginUserContext requireSite(String siteCode);

    /** 獲取當前用戶上下文，供記錄操作人和跨表業務使用。 */
    LoginUserContext currentUser();
}
