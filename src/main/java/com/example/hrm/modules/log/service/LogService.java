package com.example.hrm.modules.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.log.entity.Log;

public interface LogService {
    void saveLog(String username, String operation, String method, String params, String ip);
    Page<Log> listLogs(Integer pageNum, Integer pageSize);
}