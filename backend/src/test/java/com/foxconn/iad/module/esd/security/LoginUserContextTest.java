package com.foxconn.iad.module.esd.security;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginUserContextTest {

    @Test
    /** 平台的逗號分隔廠區字段會被去空格、去重並保持順序。 */
    void parseSitesTrimsAndDeduplicates() {
        Set<String> sites = LoginUserContext.parseSites(" ZZ, WH,ZZ, ,WH ");

        assertThat(sites).containsExactly("ZZ", "WH");
    }

    @Test
    /** 服務端只能訪問登錄上下文中配置的廠區。 */
    void canAccessOnlyConfiguredSite() {
        LoginUserContext context = new LoginUserContext(
                100L, 1L, LoginUserContext.parseSites("ZZ,WH"), false);

        assertThat(context.canAccessSite("ZZ")).isTrue();
        assertThat(context.canAccessSite("SZ")).isFalse();
    }
}
