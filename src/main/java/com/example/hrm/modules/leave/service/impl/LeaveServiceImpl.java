package com.example.hrm.modules.leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.leave.entity.LeaveApplication;
import com.example.hrm.modules.leave.mapper.LeaveMapper;
import com.example.hrm.modules.leave.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveMapper leaveMapper;

    @Override
    public Page<LeaveApplication> listLeaves(Integer pageNum, Integer pageSize, Long employeeId, String status) {
        Page<LeaveApplication> page = new Page<>(pageNum, pageSize);

        // ✅ 如果指定了员工ID，用普通查询
        if (employeeId != null) {
            LambdaQueryWrapper<LeaveApplication> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LeaveApplication::getEmployeeId, employeeId);
            if (StringUtils.hasText(status)) {
                wrapper.eq(LeaveApplication::getStatus, status);
            }
            wrapper.orderByDesc(LeaveApplication::getCreateTime);
            return leaveMapper.selectPage(page, wrapper);
        }

        // ✅ 没有指定员工ID，用关联查询获取员工姓名
        return leaveMapper.selectPageWithUser(page);
    }

    @Override
    public LeaveApplication getLeaveById(Long id) {
        return leaveMapper.selectById(id);
    }

    @Override
    public String applyLeave(LeaveApplication leave) {
        // 计算请假天数
        long hours = ChronoUnit.HOURS.between(leave.getStartTime(), leave.getEndTime());
        leave.setDays(Math.round(hours / 8.0 * 10) / 10.0);
        leave.setStatus("待审批");
        leaveMapper.insert(leave);
        return "请假申请提交成功，等待审批";
    }

    @Override
    public String approveLeave(Long id, Long approverId, String status, String remark) {
        LeaveApplication leave = leaveMapper.selectById(id);
        if (leave == null) {
            return "请假申请不存在";
        }
        if (!"待审批".equals(leave.getStatus())) {
            return "该申请已被处理";
        }
        leave.setStatus(status);
        leave.setApproverId(approverId);
        leave.setApproveTime(LocalDateTime.now());
        leave.setRemark(remark);
        leaveMapper.updateById(leave);
        return "审批完成：" + ("已通过".equals(status) ? "✅ 已批准" : "❌ 已驳回");
    }

    @Override
    public Page<LeaveApplication> getPendingApprovals(Integer pageNum, Integer pageSize) {
        Page<LeaveApplication> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LeaveApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaveApplication::getStatus, "待审批")
                .orderByDesc(LeaveApplication::getCreateTime);
        return leaveMapper.selectPage(page, wrapper);
    }
}