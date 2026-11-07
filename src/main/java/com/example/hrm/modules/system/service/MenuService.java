package com.example.hrm.modules.system.service;

import com.example.hrm.modules.system.entity.Menu;
import java.util.List;

public interface MenuService {
    List<Menu> getMenusByUserId(Long userId);
}