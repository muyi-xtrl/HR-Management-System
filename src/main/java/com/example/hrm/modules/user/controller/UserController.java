package com.example.hrm.modules.user.controller;

import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.user.entity.User;
import com.example.hrm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getUserList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role) {
        Map<String, Object> result = userService.getUserList(page, size, username, name, role);
        return Result.success(result);
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> addUser(@RequestBody User user) {
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return Result.success("新增成功");
    }

    /**
     * 修改用户
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateUser(@RequestBody User user) {
        userService.updateById(user);
        return Result.success("修改成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 重置密码
     */
    @PutMapping("/reset-password/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> resetPassword(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 重置为默认密码 123456
        String defaultPassword = passwordEncoder.encode("123456");
        user.setPassword(defaultPassword);
        userService.updateById(user);
        return Result.success("密码已重置为 123456");
    }
}
