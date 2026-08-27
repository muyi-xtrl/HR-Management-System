package com.example.hrm.modules.employee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.employee.entity.Employee;
import java.util.List;

public interface EmployeeService {

    Page<Employee> listEmployees(Integer pageNum, Integer pageSize, String keyword);

    Employee getEmployeeById(Long id);

    boolean addEmployee(Employee employee);

    boolean updateEmployee(Employee employee);

    boolean deleteEmployee(Long id);

    // ✅ 新增：查询所有员工（用于导出）
    List<Employee> getAllEmployees();
}