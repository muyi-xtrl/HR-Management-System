package com.example.hrm.modules.system.service.impl;

import com.example.hrm.modules.system.entity.Menu;
import com.example.hrm.modules.system.mapper.MenuMapper;
import com.example.hrm.modules.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> getMenusByUserId(Long userId) {
        return menuMapper.findMenusByUserId(userId);
    }
}