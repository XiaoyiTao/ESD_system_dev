package com.foxconn.iad.esd.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class LoginUserContext {

    /** 统一平台登录用户 ID。 */
    private final Long userId;
    /** 租户 ID，当前本地调试固定为 1。 */
    private final Long tenantId;
    /** 当前账号可访问的厂区集合。 */
    private final Set<String> siteCodes;
    /** 是否来自本地调试头；正式网关头为 false。 */
    private final boolean localDebug;

    /** 判断账号是否拥有指定厂区权限。 */
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
    /** 将平台逗号分隔厂区字段转换为去空格、去重且不可修改的集合。 */
