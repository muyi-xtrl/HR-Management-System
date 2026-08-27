package com.example.hrm.modules.attendance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/list")
    public Result<Page<Attendance>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) Long employeeId) {
        return Result.success(attendanceService.listAttendance(pageNum, pageSize, employeeId));
    }

    @GetMapping("/{id}")
    public Result<Attendance> getById(@PathVariable Long id) {
        return Result.success(attendanceService.getAttendanceById(id));
    }

    @PostMapping("/clockIn")
    public Result<String> clockIn(@RequestParam Long employeeId) {
        return Result.success(attendanceService.clockIn(employeeId));
    }

    @PostMapping("/clockOut")
    public Result<String> clockOut(@RequestParam Long employeeId) {
        return Result.success(attendanceService.clockOut(employeeId));
    }

    @GetMapping("/today")
    public Result<Attendance> getToday(@RequestParam Long employeeId) {
        return Result.success(attendanceService.getTodayAttendance(employeeId));
    }
}