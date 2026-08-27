package com.example.hrm.modules.leave.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.leave.entity.LeaveApplication;
import com.example.hrm.modules.leave.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @GetMapping("/list")
    public Result<Page<LeaveApplication>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status) {
        return Result.success(leaveService.listLeaves(pageNum, pageSize, employeeId, status));
    }

    @GetMapping("/{id}")
    public Result<LeaveApplication> getById(@PathVariable Long id) {
        return Result.success(leaveService.getLeaveById(id));
    }

    @PostMapping("/apply")
    public Result<String> apply(@RequestBody LeaveApplication leave) {
        return Result.success(leaveService.applyLeave(leave));
    }

    @PutMapping("/approve")
    public Result<String> approve(@RequestParam Long id,
                                  @RequestParam Long approverId,
                                  @RequestParam String status,
                                  @RequestParam(required = false) String remark) {
        return Result.success(leaveService.approveLeave(id, approverId, status, remark));
    }

    @GetMapping("/pending")
    public Result<Page<LeaveApplication>> pending(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return Result.success(leaveService.getPendingApprovals(pageNum, pageSize));
    }
}