package com.example.hrm.modules.user.controller;

import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.user.entity.User;
import com.example.hrm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 获取当前用户信息
    @GetMapping("/info")
    public Result<User> getInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    // 修改个人信息
    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    // 修改密码
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestParam String oldPassword,
                                         @RequestParam String newPassword,
                                         HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userService.updatePassword(user);

        return Result.success("密码修改成功");
    }
}