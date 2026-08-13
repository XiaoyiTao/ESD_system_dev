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
    /** 平台停用用戶不能綁定為 ESD 業務人員。 */
    void rejectsDisabledUser() {
        PlatformUserResp user = new PlatformUserResp();
        user.setId(7L);
        user.setUsername("E10007");
        user.setStatus(1);
        user.setSite("ZZ");
        when(platformUserClient.getUser(7L)).thenReturn(result(user));

        assertThatThrownBy(() -> gateway.requireEnabledUser(7L, "ZZ"))
                .hasMessage("平台用戶已停用：E10007");
    }

    @Test
    /** 平台用戶必須擁有當前綁定廠區權限。 */
    void rejectsUserOutsideSite() {
        PlatformUserResp user = new PlatformUserResp();
        user.setId(7L);
        user.setUsername("E10007");
        user.setStatus(0);
        user.setSite("WH");
        when(platformUserClient.getUser(7L)).thenReturn(result(user));

        assertThatThrownBy(() -> gateway.requireEnabledUser(7L, "ZZ"))
                .hasMessage("平台用戶不屬於廠區：ZZ");
    }

    /** 構造平台 CommonResult 成功響應，隔離 Feign DTO 細節。 */
    private PlatformRpcResult<PlatformUserResp> result(PlatformUserResp user) {
        PlatformRpcResult<PlatformUserResp> result = new PlatformRpcResult<>();
        result.setCode(0);
        result.setData(user);
        return result;
    }
}
