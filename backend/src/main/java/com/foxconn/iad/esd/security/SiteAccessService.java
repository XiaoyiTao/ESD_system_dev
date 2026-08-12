package com.foxconn.iad.esd.security;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteAccessService {

    /** 负责从网关头或本地调试头解析当前用户。 */
    private final LoginUserContextResolver loginUserContextResolver;

    /** 校验请求厂区属于当前登录用户，所有业务服务必须先调用此方法。 */
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
        // 不能仅相信前端传入的 siteCode，服务端必须以统一登录上下文为准。
    /** 获取当前用户上下文，供记录操作人和跨表业务使用。 */
