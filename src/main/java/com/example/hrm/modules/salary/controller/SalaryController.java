package com.example.hrm.modules.salary.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.salary.entity.Salary;
import com.example.hrm.modules.salary.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @GetMapping("/list")
    public Result<Page<Salary>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) Long employeeId) {
        return Result.success(salaryService.listSalaries(pageNum, pageSize, employeeId));
    }

    @GetMapping("/{id}")
    public Result<Salary> getById(@PathVariable Long id) {
        return Result.success(salaryService.getSalaryById(id));
    }

    @GetMapping("/get")
    public Result<Salary> getByEmployeeAndMonth(
            @RequestParam Long employeeId,
            @RequestParam String month) {
        return Result.success(salaryService.getSalaryByEmployeeIdAndMonth(employeeId, month));
    }

    @PostMapping("/generate")
    public Result<String> generate(@RequestBody Salary salary) {
        return Result.success(salaryService.generateSalary(salary));
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Salary salary) {
        return Result.success(salaryService.updateSalary(salary));
    }

    @PutMapping("/pay/{id}")
    public Result<String> pay(@PathVariable Long id) {
        return Result.success(salaryService.paySalary(id));
    }
}