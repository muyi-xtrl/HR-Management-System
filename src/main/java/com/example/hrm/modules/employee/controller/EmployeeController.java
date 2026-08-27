package com.example.hrm.modules.employee.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    public Result<Page<Employee>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Employee> page = employeeService.listEmployees(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        return Result.success(employee);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Employee employee) {
        boolean success = employeeService.addEmployee(employee);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Employee employee) {
        boolean success = employeeService.updateEmployee(employee);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = employeeService.deleteEmployee(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Employee> list = employeeService.getAllEmployees();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("员工数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Employee.class).sheet("员工列表").doWrite(list);
    }
}