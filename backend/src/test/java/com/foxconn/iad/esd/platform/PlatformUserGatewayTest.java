package com.foxconn.iad.esd.platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformUserGatewayTest {

    @Mock
    private PlatformUserClient platformUserClient;

    @InjectMocks
    private PlatformUserGateway gateway;

    @Test
    /** 平台停用用户不能绑定为 ESD 业务人员。 */
    void rejectsDisabledUser() {
        PlatformUserResp user = new PlatformUserResp();
        user.setId(7L);
        user.setUsername("E10007");
        user.setStatus(1);
        user.setSite("ZZ");
        when(platformUserClient.getUser(7L)).thenReturn(result(user));

        assertThatThrownBy(() -> gateway.requireEnabledUser(7L, "ZZ"))
                .hasMessage("平台用户已停用：E10007");
    }

    @Test
    /** 平台用户必须拥有当前绑定厂区权限。 */
    void rejectsUserOutsideSite() {
        PlatformUserResp user = new PlatformUserResp();
        user.setId(7L);
        user.setUsername("E10007");
        user.setStatus(0);
        user.setSite("WH");
        when(platformUserClient.getUser(7L)).thenReturn(result(user));

        assertThatThrownBy(() -> gateway.requireEnabledUser(7L, "ZZ"))
                .hasMessage("平台用户不属于厂区：ZZ");
    }

    /** 构造平台 CommonResult 成功响应，隔离 Feign DTO 细节。 */
    private PlatformRpcResult<PlatformUserResp> result(PlatformUserResp user) {
        PlatformRpcResult<PlatformUserResp> result = new PlatformRpcResult<>();
        result.setCode(0);
        result.setData(user);
        return result;
    }
}
