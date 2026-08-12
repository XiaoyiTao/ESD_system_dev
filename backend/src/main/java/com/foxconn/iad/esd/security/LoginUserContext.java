package com.foxconn.iad.esd.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class LoginUserContext {

    private final Long userId;
    private final Long tenantId;
    private final Set<String> siteCodes;
    private final boolean localDebug;

    public boolean canAccessSite(String siteCode) {
        return siteCode != null && siteCodes.contains(siteCode);
    }

    public static Set<String> parseSites(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> sites = new LinkedHashSet<>();
        for (String part : value.split(",")) {
            String site = part.trim();
            if (!site.isEmpty()) {
                sites.add(site);
            }
        }
        return Collections.unmodifiableSet(sites);
    }
}

