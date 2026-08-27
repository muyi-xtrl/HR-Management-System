package com.example.hrm.modules.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.mapper.EmployeeMapper;
import com.example.hrm.modules.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.net.URLEncoder;  // ✅ 添加这行
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Page<Employee> listEmployees(Integer pageNum, Integer pageSize, String keyword) {
        Page<Employee> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Employee::getName, keyword)
                    .or()
                    .like(Employee::getEmployeeNo, keyword);
        }

        wrapper.orderByDesc(Employee::getCreateTime);

        return employeeMapper.selectPage(page, wrapper);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeMapper.selectById(id);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return employeeMapper.insert(employee) > 0;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        return employeeMapper.updateById(employee) > 0;
    }

    @Override
    public boolean deleteEmployee(Long id) {
        // ✅ 使用 LambdaUpdateWrapper 手动更新
        LambdaUpdateWrapper<Employee> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Employee::getId, id)
                .set(Employee::getDeleted, 1);
        return employeeMapper.update(null, wrapper) > 0;
    }
    @Override
    public List<Employee> getAllEmployees() {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getDeleted, 0);
        wrapper.orderByDesc(Employee::getCreateTime);
        return employeeMapper.selectList(wrapper);
    }
}