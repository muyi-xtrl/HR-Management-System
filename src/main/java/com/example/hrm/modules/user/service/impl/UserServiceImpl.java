package com.example.hrm.modules.user.service.impl;

import com.example.hrm.modules.user.entity.User;
import com.example.hrm.modules.user.mapper.UserMapper;
import com.example.hrm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean updatePassword(User user) {
        return userMapper.updateById(user) > 0;
    }
}