package com.foxconn.iad.esd.platform;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformUserGateway {

    private static final int PLATFORM_USER_ENABLED = 0;

    private final PlatformUserClient platformUserClient;

    public PlatformUserResp requireEnabledUser(Long userId, String siteCode) {
        PlatformRpcResult<PlatformUserResp> result;
        try {
            result = platformUserClient.getUser(userId);
        } catch (Exception exception) {
            throw new BusinessException(503, "统一用户服务暂时不可用");
        }
        if (result == null || result.getCode() == null || result.getCode() != 0
                || result.getData() == null) {
            throw new BusinessException(400, "平台用户不存在：" + userId);
        }
        PlatformUserResp user = result.getData();
        if (!Integer.valueOf(PLATFORM_USER_ENABLED).equals(user.getStatus())) {
            throw new BusinessException(400, "平台用户已停用：" + user.getUsername());
        }
        if (!user.getSiteCodes().contains(siteCode)) {
            throw new BusinessException(400, "平台用户不属于厂区：" + siteCode);
        }
        return user;
    }
}

