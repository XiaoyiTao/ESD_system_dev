package com.foxconn.iad.esd.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class LoginUserContext {

    /** 統一平台登錄用戶 ID。 */
    private final Long userId;
    /** 租戶 ID，當前本地調試固定為 1。 */
    private final Long tenantId;
    /** 當前帳號可訪問的廠區集合。 */
    private final Set<String> siteCodes;
    /** 是否來自本地調試頭；正式網關頭為 false。 */
    private final boolean localDebug;

    /** 判斷帳號是否擁有指定廠區權限。 */
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
    /** 將平台逗號分隔廠區字段轉換為去空格、去重且不可修改的集合。 */
