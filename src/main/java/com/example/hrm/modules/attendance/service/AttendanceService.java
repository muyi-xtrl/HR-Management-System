package com.example.hrm.modules.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.attendance.entity.Attendance;

public interface AttendanceService {
    Page<Attendance> listAttendance(Integer pageNum, Integer pageSize, Long employeeId);
    Attendance getAttendanceById(Long id);
    String clockIn(Long employeeId);
    String clockOut(Long employeeId);
    Attendance getTodayAttendance(Long employeeId);
}