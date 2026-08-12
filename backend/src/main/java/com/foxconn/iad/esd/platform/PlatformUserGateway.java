package com.foxconn.iad.esd.platform;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformUserGateway {

    /** platform-system 的 CommonStatusEnum.ENABLE 稳定值。 */
    private static final int PLATFORM_USER_ENABLED = 0;

    /** Feign 用户 RPC 客户端。 */
    private final PlatformUserClient platformUserClient;

    /** 查询并校验用户存在、启用且属于指定厂区。 */
    public PlatformUserResp requireEnabledUser(Long userId, String siteCode) {
        PlatformRpcResult<PlatformUserResp> result;
        try {
            result = platformUserClient.getUser(userId);
        } catch (Exception exception) {
            // 对 Feign、网络和服务发现异常统一返回 503，避免误报“用户不存在”。
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
        // 平台用户可以配置多个厂区，绑定时必须命中当前业务厂区。
        if (!user.getSiteCodes().contains(siteCode)) {
            throw new BusinessException(400, "平台用户不属于厂区：" + siteCode);
        }
        return user;
    }
}
