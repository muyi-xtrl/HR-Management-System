package com.example.hrm.modules.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrm.modules.attendance.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {

    @Select("SELECT * FROM attendance WHERE employee_id = #{employeeId} AND date = #{date}")
    Attendance findByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Select("SELECT * FROM attendance WHERE employee_id = #{employeeId} AND DATE_FORMAT(date, '%Y-%m') = #{month} ORDER BY date DESC")
    List<Attendance> findByEmployeeIdAndMonth(@Param("employeeId") Long employeeId, @Param("month") String month);
}