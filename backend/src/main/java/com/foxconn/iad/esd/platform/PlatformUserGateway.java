package com.foxconn.iad.esd.platform;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformUserGateway {

    /** platform-system 的 CommonStatusEnum.ENABLE 穩定值。 */
    private static final int PLATFORM_USER_ENABLED = 0;

    /** Feign 用戶 RPC 客戶端。 */
    private final PlatformUserClient platformUserClient;

    /** 查詢並校驗用戶存在、啟用且屬於指定廠區。 */
    public PlatformUserResp requireEnabledUser(Long userId, String siteCode) {
        PlatformRpcResult<PlatformUserResp> result;
        try {
            result = platformUserClient.getUser(userId);
        } catch (Exception exception) {
            // 對 Feign、網絡和服務發現異常統一返回 503，避免誤報“用戶不存在”。
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
        // 平台用戶可以配置多個廠區，綁定時必須命中當前業務廠區。
        if (!user.getSiteCodes().contains(siteCode)) {
            throw new BusinessException(400, "平台用户不属于厂区：" + siteCode);
        }
        return user;
    }
}
