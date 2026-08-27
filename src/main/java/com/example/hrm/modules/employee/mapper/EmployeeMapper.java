package com.example.hrm.modules.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrm.modules.employee.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    // BaseMapper 已经提供了增删改查方法，不用自己写
}