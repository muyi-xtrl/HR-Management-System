package com.example.hrm.modules.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.log.entity.Log;
import com.example.hrm.modules.log.mapper.LogMapper;
import com.example.hrm.modules.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public void saveLog(String username, String operation, String method, String params, String ip) {
        Log log = new Log();
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        logMapper.insert(log);
    }

    @Override
    public Page<Log> listLogs(Integer pageNum, Integer pageSize) {
        Page<Log> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Log::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }
}