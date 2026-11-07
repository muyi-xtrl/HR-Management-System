package com.example.hrm.modules.user.controller;

import com.example.hrm.common.utils.JwtUtils;
import com.example.hrm.common.utils.RedisUtils;
import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.user.entity.User;
import com.example.hrm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtils redisUtils;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {

        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String captcha = loginDTO.getCaptcha();
        String captchaKey = loginDTO.getCaptchaKey();

        // 1. 从 Redis 获取验证码
        Object redisCaptchaObj = redisUtils.get("captcha:" + captchaKey);
        if (redisCaptchaObj == null) {
            return Result.error("验证码已过期");
        }
        String redisCaptcha = redisCaptchaObj.toString();
        if (!redisCaptcha.equalsIgnoreCase(captcha)) {
            return Result.error("验证码错误");
        }
        redisUtils.delete("captcha:" + captchaKey);

        // 2. 检查是否被锁定
        String lockKey = "login:lock:" + username;
        if (redisUtils.hasKey(lockKey)) {
            long expire = redisUtils.getExpire(lockKey);
            return Result.error("账号已被锁定，请 " + expire + " 秒后再试");
        }

        // 3. 查询用户
        User user = userService.findByUsername(username);
        if (user == null) {
            recordLoginFailure(username);
            return Result.error("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            recordLoginFailure(username);
            return Result.error("密码错误");
        }

        // 5. 登录成功，清除失败记录
        String failKey = "login:fail:" + username;
        redisUtils.delete(failKey);

        // 6. 生成 Token（包含角色）
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 7. 返回用户信息和 Token
        user.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * ✅ 刷新 Token
     */
    @PostMapping("/refresh")
    public Result<?> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("无效的Token");
        }

        String oldToken = authHeader.substring(7);
        try {
            // 验证旧 Token 是否有效
            if (!jwtUtils.validateToken(oldToken)) {
                return Result.error("Token已过期，请重新登录");
            }

            // 从旧 Token 中提取用户信息
            Long userId = jwtUtils.getUserIdFromToken(oldToken);
            String username = jwtUtils.getUsernameFromToken(oldToken);
            String role = jwtUtils.getRoleFromToken(oldToken);

            // 生成新 Token
            String newToken = jwtUtils.generateToken(userId, username, role);

            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Token刷新失败，请重新登录");
        }
    }

    /**
     * 记录登录失败次数
     */
    private void recordLoginFailure(String username) {
        String failKey = "login:fail:" + username;
        Integer attempts = redisUtils.get(failKey, Integer.class);
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;
        redisUtils.set(failKey, attempts, 1, TimeUnit.HOURS);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            String lockKey = "login:lock:" + username;
            redisUtils.set(lockKey, "locked", LOCK_TIME_MINUTES, TimeUnit.MINUTES);
            redisUtils.delete(failKey);
        }
    }
}