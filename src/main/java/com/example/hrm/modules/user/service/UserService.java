package com.example.hrm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hrm.modules.user.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    User findByUsername(String username);

    /**
     * 分页查询用户列表
     */
    Map<String, Object> getUserList(Integer page, Integer size, String username, String name, String role);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String newPassword);
}