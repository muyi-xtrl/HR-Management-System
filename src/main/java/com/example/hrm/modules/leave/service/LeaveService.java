package com.example.hrm.modules.leave.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.leave.entity.LeaveApplication;

public interface LeaveService {
    Page<LeaveApplication> listLeaves(Integer pageNum, Integer pageSize, Long employeeId, String status);
    LeaveApplication getLeaveById(Long id);
    String applyLeave(LeaveApplication leave);
    String approveLeave(Long id, Long approverId, String status, String remark);
    Page<LeaveApplication> getPendingApprovals(Integer pageNum, Integer pageSize);
}