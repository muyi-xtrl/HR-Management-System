package com.example.hrm.modules.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.mapper.AttendanceMapper;
import com.example.hrm.modules.attendance.service.AttendanceService;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.mapper.EmployeeMapper;
import com.example.hrm.modules.user.entity.User;
import com.example.hrm.modules.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public void clockIn(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserId, user.getId())
                        .eq(Employee::getDeleted, 0)
        );

        if (employees == null || employees.isEmpty()) {
            throw new RuntimeException("员工信息不存在");
        }

        Employee employee = employees.get(0);

        LocalDate today = LocalDate.now();
        Attendance existing = attendanceMapper.selectOne(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getEmployeeId, employee.getId())
                        .eq(Attendance::getDate, today)
        );

        if (existing != null && existing.getCheckInTime() != null) {
            throw new RuntimeException("今日已打卡上班");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employee.getId());
        attendance.setDate(today);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus("正常");

        if (existing != null) {
            attendance.setId(existing.getId());
            attendanceMapper.updateById(attendance);
        } else {
            attendanceMapper.insert(attendance);
        }
    }

    @Override
    @Transactional
    public void clockOut(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserId, user.getId())
                        .eq(Employee::getDeleted, 0)
        );

        if (employees == null || employees.isEmpty()) {
            throw new RuntimeException("员工信息不存在");
        }

        Employee employee = employees.get(0);

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceMapper.selectOne(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getEmployeeId, employee.getId())
                        .eq(Attendance::getDate, today)
        );

        if (attendance == null || attendance.getCheckInTime() == null) {
            throw new RuntimeException("今日未打卡上班");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("今日已打卡下班");
        }

        LocalDateTime now = LocalDateTime.now();
        attendance.setCheckOutTime(now);

        // ✅ 计算工作时长（分钟）
        long workMinutes = java.time.Duration.between(attendance.getCheckInTime(), now).toMinutes();

        // ✅ 根据工作时长设置状态
        if (workMinutes < 30) {
            attendance.setStatus("异常");
        } else if (workMinutes < 480) {  // 480分钟 = 8小时
            attendance.setStatus("早退");
        } else {
            attendance.setStatus("正常");
        }

        attendanceMapper.updateById(attendance);
    }

    @Override
    public Attendance getTodayRecord(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }

        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserId, user.getId())
                        .eq(Employee::getDeleted, 0)
        );

        if (employees == null || employees.isEmpty()) {
            return null;
        }

        Employee employee = employees.get(0);

        return attendanceMapper.selectOne(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getEmployeeId, employee.getId())
                        .eq(Attendance::getDate, LocalDate.now())
        );
    }

    @Override
    public Map<String, Object> getHistory(String username, Integer page, Integer size, String month) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("records", new ArrayList<>());
            result.put("total", 0L);
            result.put("page", page != null ? page : 1);
            result.put("size", size != null ? size : 10);
            return result;
        }

        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserId, user.getId())
                        .eq(Employee::getDeleted, 0)
        );

        if (employees == null || employees.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("records", new ArrayList<>());
            result.put("total", 0L);
            result.put("page", page != null ? page : 1);
            result.put("size", size != null ? size : 10);
            return result;
        }

        Employee employee = employees.get(0);

        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getEmployeeId, employee.getId())
                .orderByDesc(Attendance::getDate);

        if (month != null && !month.isEmpty()) {
            try {
                String[] parts = month.split("-");
                int year = Integer.parseInt(parts[0]);
                int monthNum = Integer.parseInt(parts[1]);
                LocalDate startDate = LocalDate.of(year, monthNum, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                wrapper.between(Attendance::getDate, startDate, endDate);
            } catch (Exception e) {
                // 忽略
            }
        }

        Page<Attendance> attendancePage = attendanceMapper.selectPage(new Page<>(page, size), wrapper);

        List<Map<String, Object>> recordsWithName = attendancePage.getRecords().stream().map(record -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("employeeId", record.getEmployeeId());
            item.put("employeeName", employee.getName());
            item.put("date", record.getDate());
            item.put("checkInTime", record.getCheckInTime());
            item.put("checkOutTime", record.getCheckOutTime());
            item.put("status", record.getStatus());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", recordsWithName);
        result.put("total", attendancePage.getTotal());
        result.put("page", attendancePage.getCurrent());
        result.put("size", attendancePage.getSize());
        return result;
    }

    @Override
    public Map<String, Object> getStatistics(String username, String month) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("totalDays", 0);
            result.put("presentDays", 0);
            result.put("lateDays", 0);
            result.put("absentDays", 0);
            return result;
        }

        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserId, user.getId())
                        .eq(Employee::getDeleted, 0)
        );

        if (employees == null || employees.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("totalDays", 0);
            result.put("presentDays", 0);
            result.put("lateDays", 0);
            result.put("absentDays", 0);
            return result;
        }

        Employee employee = employees.get(0);

        if (month == null || month.isEmpty()) {
            month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        LocalDate startDate, endDate;
        try {
            String[] parts = month.split("-");
            int year = Integer.parseInt(parts[0]);
            int monthNum = Integer.parseInt(parts[1]);
            startDate = LocalDate.of(year, monthNum, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } catch (Exception e) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        List<Attendance> records = attendanceMapper.selectList(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getEmployeeId, employee.getId())
                        .between(Attendance::getDate, startDate, endDate)
        );

        long totalDays = records.size();
        long presentDays = records.stream()
                .filter(r -> r.getCheckInTime() != null)
                .count();
        long lateDays = records.stream()
                .filter(r -> "迟到".equals(r.getStatus()))
                .count();
        long absentDays = totalDays - presentDays;

        Map<String, Object> result = new HashMap<>();
        result.put("totalDays", totalDays);
        result.put("presentDays", presentDays);
        result.put("lateDays", lateDays);
        result.put("absentDays", absentDays);
        return result;
    }

    @Override
    public Map<String, Object> getAllAttendance(Integer page, Integer size, String month, Long employeeId) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<Attendance>()
                .orderByDesc(Attendance::getDate);

        if (employeeId != null && employeeId > 0) {
            wrapper.eq(Attendance::getEmployeeId, employeeId);
        }

        if (month != null && !month.isEmpty()) {
            try {
                String[] parts = month.split("-");
                int year = Integer.parseInt(parts[0]);
                int monthNum = Integer.parseInt(parts[1]);
                LocalDate startDate = LocalDate.of(year, monthNum, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                wrapper.between(Attendance::getDate, startDate, endDate);
            } catch (Exception e) {
                // 忽略
            }
        }

        Page<Attendance> attendancePage = attendanceMapper.selectPage(new Page<>(page, size), wrapper);

        List<Attendance> records = attendancePage.getRecords();
        if (records.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("records", new ArrayList<>());
            result.put("total", 0L);
            result.put("page", page);
            result.put("size", size);
            return result;
        }

        List<Long> employeeIds = records.stream()
                .map(Attendance::getEmployeeId)
                .distinct()
                .collect(Collectors.toList());

        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .in(Employee::getId, employeeIds)
                        .eq(Employee::getDeleted, 0)
        );
        Map<Long, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));

        List<Map<String, Object>> resultList = records.stream().map(record -> {
            Map<String, Object> item = new HashMap<>();
            Employee emp = employeeMap.get(record.getEmployeeId());
            item.put("id", record.getId());
            item.put("employeeId", record.getEmployeeId());
            item.put("employeeName", emp != null ? emp.getName() : "未知");
            item.put("date", record.getDate() != null ? record.getDate().toString() : null);
            item.put("checkInTime", record.getCheckInTime() != null ? record.getCheckInTime().toString() : null);
            item.put("checkOutTime", record.getCheckOutTime() != null ? record.getCheckOutTime().toString() : null);
            item.put("status", record.getStatus() != null ? record.getStatus() : "正常");
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", resultList);
        result.put("total", attendancePage.getTotal());
        result.put("page", attendancePage.getCurrent());
        result.put("size", attendancePage.getSize());
        return result;
    }
}