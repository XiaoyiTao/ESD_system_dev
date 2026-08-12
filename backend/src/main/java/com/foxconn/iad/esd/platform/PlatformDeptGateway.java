package com.foxconn.iad.esd.platform;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformDeptGateway {

    /** platform-system 的 CommonStatusEnum.ENABLE 稳定值。 */
    private static final int PLATFORM_DEPT_ENABLED = 0;

    /** Feign 部门 RPC 客户端。 */
    private final PlatformDeptClient platformDeptClient;

    /** 查询并校验部门启用状态；无部门的用户允许返回空快照。 */
    public PlatformDeptResp requireEnabledDept(Long deptId) {
        if (deptId == null) {
            return null;
        }
        PlatformRpcResult<PlatformDeptResp> result;
        try {
            result = platformDeptClient.getDept(deptId);
        } catch (Exception exception) {
            // 平台异常不能降级为本地空部门，否则会产生错误组织快照。
            throw new BusinessException(503, "统一部门服务暂时不可用");
        }
        if (result == null || !Integer.valueOf(0).equals(result.getCode()) || result.getData() == null) {
            throw new BusinessException(400, "平台部门不存在：" + deptId);
        }
        if (!Integer.valueOf(PLATFORM_DEPT_ENABLED).equals(result.getData().getStatus())) {
            throw new BusinessException(400, "平台部门已停用：" + result.getData().getName());
        }
        return result.getData();
    }
}
