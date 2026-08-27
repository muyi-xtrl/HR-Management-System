package com.example.hrm.modules.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.department.entity.Department;
import com.example.hrm.modules.department.mapper.DepartmentMapper;
import com.example.hrm.modules.department.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public Page<Department> listDepartments(Integer pageNum, Integer pageSize, String keyword) {
        Page<Department> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Department::getName, keyword);
        }
        wrapper.orderByAsc(Department::getSort);
        return departmentMapper.selectPage(page, wrapper);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public boolean addDepartment(Department department) {
        return departmentMapper.insert(department) > 0;
    }

    @Override
    public boolean updateDepartment(Department department) {
        return departmentMapper.updateById(department) > 0;
    }

    @Override
    public boolean deleteDepartment(Long id) {
        LambdaUpdateWrapper<Department> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Department::getId, id)
                .set(Department::getDeleted, 1);
        return departmentMapper.update(null, wrapper) > 0;
    }
}