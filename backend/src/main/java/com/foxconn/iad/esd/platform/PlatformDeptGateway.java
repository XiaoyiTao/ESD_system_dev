package com.foxconn.iad.esd.platform;

import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformDeptGateway {

    private static final int PLATFORM_DEPT_ENABLED = 0;

    private final PlatformDeptClient platformDeptClient;

    public PlatformDeptResp requireEnabledDept(Long deptId) {
        if (deptId == null) {
            return null;
        }
        PlatformRpcResult<PlatformDeptResp> result;
        try {
            result = platformDeptClient.getDept(deptId);
        } catch (Exception exception) {
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
