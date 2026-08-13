package com.foxconn.iad.esd.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.iad.esd.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class LoginUserContextResolver {

    /** 網關寫入的 URL 編碼 JSON 登錄上下文。 */
    private static final String LOGIN_USER_HEADER = "login-user";
    /** 本地開發專用用戶 ID 頭，生產配置必須關閉調試模式。 */
    private static final String DEBUG_USER_HEADER = "X-ESD-Debug-User-Id";
    private static final String DEBUG_SITE_HEADER = "X-ESD-Debug-Sites";

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    @Value("${esd.local-debug-auth-enabled:false}")
    private boolean localDebugAuthEnabled;

    public LoginUserContext requireCurrentUser() {
        String encodedLoginUser = request.getHeader(LOGIN_USER_HEADER);
        if (encodedLoginUser != null && !encodedLoginUser.trim().isEmpty()) {
            return parsePlatformHeader(encodedLoginUser);
        }
        if (localDebugAuthEnabled) {
            return parseDebugHeaders();
        }
        throw new BusinessException(401, "未登录或登录信息已失效");
    }

    private LoginUserContext parsePlatformHeader(String encodedLoginUser) {
        try {
            String json = URLDecoder.decode(encodedLoginUser, StandardCharsets.UTF_8.name());
            JsonNode root = objectMapper.readTree(json);
            Long userId = requiredLong(root, "id");
            Long tenantId = optionalLong(root, "tenantId");
            String sites = root.path("info").path("site").asText("");
            LoginUserContext context = new LoginUserContext(
                    userId, tenantId, LoginUserContext.parseSites(sites), false);
            if (context.getSiteCodes().isEmpty()) {
                throw new BusinessException(403, "当前账号未配置厂区权限");
            }
            return context;
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(401, "无法解析统一登录信息");
        }
    }

    private LoginUserContext parseDebugHeaders() {
        String rawUserId = request.getHeader(DEBUG_USER_HEADER);
        String sites = request.getHeader(DEBUG_SITE_HEADER);
        if (rawUserId == null || sites == null) {
            throw new BusinessException(401,
                    "本地调试请提供 X-ESD-Debug-User-Id 和 X-ESD-Debug-Sites");
        }
        try {
            LoginUserContext context = new LoginUserContext(Long.valueOf(rawUserId), 1L,
                    LoginUserContext.parseSites(sites), true);
            if (context.getSiteCodes().isEmpty()) {
                throw new BusinessException(403, "本地调试厂区不能为空");
            }
            return context;
        } catch (NumberFormatException exception) {
            throw new BusinessException(400, "本地调试用户编号格式错误");
        }
    }

    private Long requiredLong(JsonNode root, String field) {
        Long value = optionalLong(root, field);
        if (value == null) {
            throw new BusinessException(401, "统一登录信息缺少用户编号");
        }
        return value;
    }

    private Long optionalLong(JsonNode root, String field) {
        JsonNode node = root.path(field);
        return node.isNumber() ? node.longValue() : null;
    }
}
    /** 本地開發專用廠區權限頭。 */
    /** 當前 HTTP 請求，用於讀取網關轉發的認證頭。 */
    /** 復用 Spring Boot 的 Jackson 配置解析登錄 JSON。 */
    /** 是否允許使用本地調試頭，默認關閉。 */
    /** 優先解析真實平台上下文，只有明確開啟本地調試時才接受調試頭。 */
        // 真實網關頭優先，避免調試開關影響已經認證的生產請求。
    /** 解析 gateway LoginUser 的 URL 編碼 JSON。 */
            // 平台登錄上下文的 site 位於 info.site，多個廠區使用逗號分隔。
    /** 解析本機聯調使用的簡化認證頭，不代表正式鑑權方案。 */
            // 本地請求沒有租戶網關時，使用固定租戶值僅方便業務聯調。
    /** 讀取必填的數值字段並轉換成統一業務錯誤。 */
    /** 讀取可選數值字段；缺失或非數字時返回 null。 */
