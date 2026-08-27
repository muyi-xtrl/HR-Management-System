package com.example.hrm.modules.user.controller;

import com.example.hrm.common.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("人事管理系统 API 运行正常！");
    }
}