package com.example.hrm.modules.department.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.department.entity.Department;

public interface DepartmentService {
    Page<Department> listDepartments(Integer pageNum, Integer pageSize, String keyword);
    Department getDepartmentById(Long id);
    boolean addDepartment(Department department);
    boolean updateDepartment(Department department);
    boolean deleteDepartment(Long id);
}