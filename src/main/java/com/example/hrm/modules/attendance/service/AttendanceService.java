package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.entity.Attendance;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    void clockIn(String username);

    void clockOut(String username);

    Attendance getTodayRecord(String username);

    Map<String, Object> getHistory(String username, Integer page, Integer size, String month);

    Map<String, Object> getStatistics(String username, String month);

    Map<String, Object> getAllAttendance(Integer page, Integer size, String month, Long employeeId);
}