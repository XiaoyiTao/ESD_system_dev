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

    private PlatformRpcResult<PlatformUserResp> result(PlatformUserResp user) {
        PlatformRpcResult<PlatformUserResp> result = new PlatformRpcResult<>();
        result.setCode(0);
        result.setData(user);
        return result;
    }
}
