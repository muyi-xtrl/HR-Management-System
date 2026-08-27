package com.example.hrm.modules.salary.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.salary.entity.Salary;

public interface SalaryService {
    Page<Salary> listSalaries(Integer pageNum, Integer pageSize, Long employeeId);
    Salary getSalaryById(Long id);
    Salary getSalaryByEmployeeIdAndMonth(Long employeeId, String month);
    String generateSalary(Salary salary);
    String updateSalary(Salary salary);
    String paySalary(Long id);
}