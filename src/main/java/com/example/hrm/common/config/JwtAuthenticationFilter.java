package com.example.hrm.common.config;

import com.example.hrm.common.utils.JwtUtils;
import com.example.hrm.modules.log.service.LogService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LogService logService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtUtils.getUsername(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, null);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // 将用户名存入 request 属性
                request.setAttribute("username", username);

                // 记录操作日志（排除登录接口，避免死循环）
                String requestURI = request.getRequestURI();
                if (!requestURI.contains("/auth/login")) {
                    logService.saveLog(
                            username,
                            request.getMethod() + " " + requestURI,
                            request.getMethod(),
                            request.getQueryString() != null ? request.getQueryString() : "{}",
                            getClientIp(request)
                    );
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // ✅ 处理JWT令牌过期
            logger.warn("JWT token expired for request: " + request.getRequestURI());
            handleJwtException(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expired", "Your JWT token has expired. Please login again.");

        } catch (JwtException e) {
            // ✅ 处理其他JWT异常（如签名错误、格式错误等）
            logger.warn("JWT token invalid for request: " + request.getRequestURI() + ", error: " + e.getMessage());
            handleJwtException(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid token", "Your JWT token is invalid. Please login again.");

        } catch (Exception e) {
            // ✅ 处理其他未预期的异常
            logger.error("Unexpected error in JWT filter: " + e.getMessage(), e);
            handleJwtException(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal error", "An internal error occurred. Please try again.");
        }
    }

    /**
     * ✅ 统一处理JWT异常响应
     */
    private void handleJwtException(HttpServletResponse response, int status,
                                    String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"code\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status, error, message
        ));
        response.getWriter().flush();
    }

    /**
     * ✅ 获取真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果通过多层代理，取第一个非unknown的IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * ✅ 可选：跳过某些不需要JWT验证的路径
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // 跳过登录、注册、验证码等公开接口
        return path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/captcha") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs");
    }
}