package com.example.hrm.modules.department.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.department.entity.Department;
import com.example.hrm.modules.department.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    public Result<Page<Department>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(departmentService.listDepartments(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        Department dept = departmentService.getDepartmentById(id);
        return dept != null ? Result.success(dept) : Result.error("部门不存在");
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Department department) {
        return departmentService.addDepartment(department) ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Department department) {
        return departmentService.updateDepartment(department) ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return departmentService.deleteDepartment(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}