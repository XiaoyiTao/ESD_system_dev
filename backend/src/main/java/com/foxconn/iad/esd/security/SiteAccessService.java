package com.foxconn.iad.esd.security;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteAccessService {

    private final LoginUserContextResolver loginUserContextResolver;

    public LoginUserContext requireSite(String siteCode) {
        LoginUserContext currentUser = loginUserContextResolver.requireCurrentUser();
        if (!currentUser.canAccessSite(siteCode)) {
            throw new BusinessException(403, "无权访问厂区：" + siteCode);
        }
        return currentUser;
    }

    public LoginUserContext currentUser() {
        return loginUserContextResolver.requireCurrentUser();
    }
}

