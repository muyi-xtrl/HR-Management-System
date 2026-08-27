package com.example.hrm.modules.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.mapper.AttendanceMapper;
import com.example.hrm.modules.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public Page<Attendance> listAttendance(Integer pageNum, Integer pageSize, Long employeeId) {
        Page<Attendance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            wrapper.eq(Attendance::getEmployeeId, employeeId);
        }
        wrapper.orderByDesc(Attendance::getDate);
        return attendanceMapper.selectPage(page, wrapper);
    }

    @Override
    public Attendance getAttendanceById(Long id) {
        return attendanceMapper.selectById(id);
    }

    @Override
    public String clockIn(Long employeeId) {
        LocalDate today = LocalDate.now();
        Attendance existing = attendanceMapper.findByEmployeeIdAndDate(employeeId, today);

        if (existing != null && existing.getCheckInTime() != null) {
            return "今日已打卡上班";
        }

        if (existing == null) {
            existing = new Attendance();
            existing.setEmployeeId(employeeId);
            existing.setDate(today);
        }

        existing.setCheckInTime(LocalDateTime.now());
        existing.setStatus("正常");
        attendanceMapper.insert(existing);
        return "打卡成功，上班时间：" + LocalDateTime.now();
    }

    @Override
    public String clockOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        Attendance existing = attendanceMapper.findByEmployeeIdAndDate(employeeId, today);

        if (existing == null || existing.getCheckInTime() == null) {
            return "今日未打卡上班，无法打卡下班";
        }

        if (existing.getCheckOutTime() != null) {
            return "今日已打卡下班";
        }

        LocalDateTime now = LocalDateTime.now();
        existing.setCheckOutTime(now);

        // 计算工作时长
        long minutes = ChronoUnit.MINUTES.between(existing.getCheckInTime(), now);
        existing.setWorkHours(Math.round(minutes / 10.0) / 6.0);

        attendanceMapper.updateById(existing);
        return "打卡下班成功，下班时间：" + now + "，今日工作 " + existing.getWorkHours() + " 小时";
    }

    @Override
    public Attendance getTodayAttendance(Long employeeId) {
        return attendanceMapper.findByEmployeeIdAndDate(employeeId, LocalDate.now());
    }
}