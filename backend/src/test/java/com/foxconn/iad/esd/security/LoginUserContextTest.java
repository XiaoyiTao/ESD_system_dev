package com.foxconn.iad.esd.security;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginUserContextTest {

    @Test
    /** 平台的逗号分隔厂区字段会被去空格、去重并保持顺序。 */
    void parseSitesTrimsAndDeduplicates() {
        Set<String> sites = LoginUserContext.parseSites(" ZZ, WH,ZZ, ,WH ");

        assertThat(sites).containsExactly("ZZ", "WH");
    }

    @Test
    /** 服务端只能访问登录上下文中配置的厂区。 */
    void canAccessOnlyConfiguredSite() {
        LoginUserContext context = new LoginUserContext(
                100L, 1L, LoginUserContext.parseSites("ZZ,WH"), false);

        assertThat(context.canAccessSite("ZZ")).isTrue();
        assertThat(context.canAccessSite("SZ")).isFalse();
    }
}
