package com.foxconn.iad.esd.security;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteAccessService {

    /** 負責從網關頭或本地調試頭解析當前用戶。 */
    private final LoginUserContextResolver loginUserContextResolver;

    /** 校驗請求廠區屬於當前登錄用戶，所有業務服務必須先調用此方法。 */
    public LoginUserContext requireSite(String siteCode) {
        LoginUserContext currentUser = loginUserContextResolver.requireCurrentUser();
        if (!currentUser.canAccessSite(siteCode)) {
            throw new BusinessException(403, "無權訪問廠區：" + siteCode);
        }
        return currentUser;
    }

    public LoginUserContext currentUser() {
        return loginUserContextResolver.requireCurrentUser();
    }
}
        // 不能僅相信前端傳入的 siteCode，服務端必須以統一登錄上下文為準。
    /** 獲取當前用戶上下文，供記錄操作人和跨表業務使用。 */
