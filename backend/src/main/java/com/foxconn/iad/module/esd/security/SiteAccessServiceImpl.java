package com.foxconn.iad.module.esd.security;

import com.foxconn.iad.module.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteAccessServiceImpl implements SiteAccessService {

    /** 負責從網關頭或本地調試頭解析當前用戶。 */
    private final LoginUserContextResolver loginUserContextResolver;

    @Override
    public LoginUserContext requireSite(String siteCode) {
        LoginUserContext currentUser = loginUserContextResolver.requireCurrentUser();
        if (!currentUser.canAccessSite(siteCode)) {
            throw new BusinessException(403, "無權訪問廠區：" + siteCode);
        }
        return currentUser;
    }

    @Override
    public LoginUserContext currentUser() {
        return loginUserContextResolver.requireCurrentUser();
    }
}
