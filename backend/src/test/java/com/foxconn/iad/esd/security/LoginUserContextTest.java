package com.foxconn.iad.esd.security;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginUserContextTest {

    @Test
    void parseSitesTrimsAndDeduplicates() {
        Set<String> sites = LoginUserContext.parseSites(" ZZ, WH,ZZ, ,WH ");

        assertThat(sites).containsExactly("ZZ", "WH");
    }

    @Test
    void canAccessOnlyConfiguredSite() {
        LoginUserContext context = new LoginUserContext(
                100L, 1L, LoginUserContext.parseSites("ZZ,WH"), false);

        assertThat(context.canAccessSite("ZZ")).isTrue();
        assertThat(context.canAccessSite("SZ")).isFalse();
    }
}
