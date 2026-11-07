package com.example.hrm.modules.attendance.controller;

import com.example.hrm.common.utils.Result;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 打卡上班
     */
    @PostMapping("/clockIn")
    public Result<?> clockIn(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        attendanceService.clockIn(username);
        return Result.success("打卡成功");
    }

    /**
     * 打卡下班
     */
    @PostMapping("/clockOut")
    public Result<?> clockOut(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        attendanceService.clockOut(username);
        return Result.success("打卡成功");
    }

    /**
     * 获取今日打卡记录
     */
    @GetMapping("/today")
    public Result<?> getTodayRecord(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        Attendance record = attendanceService.getTodayRecord(username);
        return Result.success(record);
    }

    /**
     * 获取考勤历史列表（分页）
     */
    @GetMapping("/list")
    public Result<?> getHistory(
            HttpServletRequest request,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String month) {
        String username = (String) request.getAttribute("username");
        Map<String, Object> result = attendanceService.getHistory(username, page, size, month);
        return Result.success(result);
    }

    /**
     * ✅ 获取所有考勤记录（管理员专用）- 保留这一个
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getAllAttendance(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Long employeeId) {
        Map<String, Object> result = attendanceService.getAllAttendance(page, size, month, employeeId);
        return Result.success(result);
    }

    /**
     * 获取考勤统计
     */
    @GetMapping("/statistics")
    public Result<?> getStatistics(
            HttpServletRequest request,
            @RequestParam(required = false) String month) {
        String username = (String) request.getAttribute("username");
        if (month == null || month.isEmpty()) {
            month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        Map<String, Object> stats = attendanceService.getStatistics(username, month);
        return Result.success(stats);
    }
}