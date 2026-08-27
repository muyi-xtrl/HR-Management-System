package com.example.hrm.modules.user.service;

import com.example.hrm.modules.user.entity.User;

public interface UserService {
    User findByUsername(String username);
    boolean updateUser(User user);
    boolean updatePassword(User user);
}