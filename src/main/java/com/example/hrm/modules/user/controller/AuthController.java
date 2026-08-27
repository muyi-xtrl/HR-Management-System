package com.example.hrm.modules.user.controller;

import com.example.hrm.common.utils.JwtUtils;
import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.user.entity.User;
import com.example.hrm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
                                @RequestParam String password) {

        // 1. 从数据库查询用户
        User user = userService.findByUsername(username);

        // 2. 用户不存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 用户被禁用
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        // 4. 验证密码（暂时明文对比，后面改成 BCrypt）
        if (!password.equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        // 5. 登录成功，生成 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        return Result.success("登录成功", token);
    }
}