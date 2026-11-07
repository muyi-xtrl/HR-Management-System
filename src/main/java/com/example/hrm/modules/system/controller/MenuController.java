package com.example.hrm.modules.system.controller;

import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.system.entity.Menu;
import com.example.hrm.modules.system.service.MenuService;
import com.example.hrm.modules.user.entity.User;
import com.example.hrm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public Result<List<Menu>> getCurrentUserMenus(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        Long userId = 1L; // 默认值
        if (username != null) {
            // ✅ 显式声明类型，不用 var
            User user = userService.findByUsername(username);
            if (user != null) {
                userId = user.getId();
            }
        }

        List<Menu> menus = menuService.getMenusByUserId(userId);
        return Result.success(menus);
    }
}