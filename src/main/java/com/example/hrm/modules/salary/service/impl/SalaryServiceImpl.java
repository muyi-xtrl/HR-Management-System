package com.example.hrm.modules.salary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.salary.entity.Salary;
import com.example.hrm.modules.salary.mapper.SalaryMapper;
import com.example.hrm.modules.salary.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryMapper salaryMapper;

    @Override
    public Page<Salary> listSalaries(Integer pageNum, Integer pageSize, Long employeeId) {
        Page<Salary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Salary> wrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            wrapper.eq(Salary::getEmployeeId, employeeId);
        }
        wrapper.orderByDesc(Salary::getMonth);
        return salaryMapper.selectPage(page, wrapper);
    }

    @Override
    public Salary getSalaryById(Long id) {
        return salaryMapper.selectById(id);
    }

    @Override
    public Salary getSalaryByEmployeeIdAndMonth(Long employeeId, String month) {
        return salaryMapper.findByEmployeeIdAndMonth(employeeId, month);
    }

    @Override
    public String generateSalary(Salary salary) {
        // 计算实发工资
        double actual = salary.getBaseSalary() + salary.getBonus() + salary.getAllowance()
                + salary.getOvertimePay() - salary.getSocialSecurity() - salary.getTax();
        salary.setActualSalary(Math.round(actual * 100) / 100.0);
        salary.setStatus("待发放");
        salaryMapper.insert(salary);
        return "薪资生成成功，金额：" + salary.getActualSalary();
    }

    @Override
    public String updateSalary(Salary salary) {
        salaryMapper.updateById(salary);
        return "薪资修改成功";
    }

    @Override
    public String paySalary(Long id) {
        Salary salary = salaryMapper.selectById(id);
        if (salary == null) {
            return "薪资记录不存在";
        }
        if ("已发放".equals(salary.getStatus())) {
            return "薪资已发放，不能重复操作";
        }
        salary.setStatus("已发放");
        salaryMapper.updateById(salary);
        return "薪资发放成功";
    }
}