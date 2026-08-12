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

    /** 网关写入的 URL 编码 JSON 登录上下文。 */
    private static final String LOGIN_USER_HEADER = "login-user";
    /** 本地开发专用用户 ID 头，生产配置必须关闭调试模式。 */
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
    /** 本地开发专用厂区权限头。 */
    /** 当前 HTTP 请求，用于读取网关转发的认证头。 */
    /** 复用 Spring Boot 的 Jackson 配置解析登录 JSON。 */
    /** 是否允许使用本地调试头，默认关闭。 */
    /** 优先解析真实平台上下文，只有明确开启本地调试时才接受调试头。 */
        // 真实网关头优先，避免调试开关影响已经认证的生产请求。
    /** 解析 gateway LoginUser 的 URL 编码 JSON。 */
            // 平台登录上下文的 site 位于 info.site，多个厂区使用逗号分隔。
    /** 解析本机联调使用的简化认证头，不代表正式鉴权方案。 */
            // 本地请求没有租户网关时，使用固定租户值仅方便业务联调。
    /** 读取必填的数值字段并转换成统一业务错误。 */
    /** 读取可选数值字段；缺失或非数字时返回 null。 */
