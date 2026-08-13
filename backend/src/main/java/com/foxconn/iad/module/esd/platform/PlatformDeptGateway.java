package com.foxconn.iad.module.esd.platform;

import com.foxconn.iad.module.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformDeptGateway {

    /** platform-system 的 CommonStatusEnum.ENABLE 穩定值。 */
    private static final int PLATFORM_DEPT_ENABLED = 0;

    /** Feign 部門 RPC 客戶端。 */
    private final PlatformDeptClient platformDeptClient;

    /** 查詢並校驗部門啟用狀態；無部門的用戶允許返回空快照。 */
    public PlatformDeptResp requireEnabledDept(Long deptId) {
        if (deptId == null) {
            return null;
        }
        PlatformRpcResult<PlatformDeptResp> result;
        try {
            result = platformDeptClient.getDept(deptId);
        } catch (Exception exception) {
            // 平台異常不能降級為本地空部門，否則會產生錯誤組織快照。
            throw new BusinessException(503, "統一部門服務暫時不可用");
        }
        if (result == null || !Integer.valueOf(0).equals(result.getCode()) || result.getData() == null) {
            throw new BusinessException(400, "平台部門不存在：" + deptId);
        }
        if (!Integer.valueOf(PLATFORM_DEPT_ENABLED).equals(result.getData().getStatus())) {
            throw new BusinessException(400, "平台部門已停用：" + result.getData().getName());
        }
        return result.getData();
    }
}
