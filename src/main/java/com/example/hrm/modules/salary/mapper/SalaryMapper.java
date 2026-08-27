package com.example.hrm.modules.salary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrm.modules.salary.entity.Salary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SalaryMapper extends BaseMapper<Salary> {

    @Select("SELECT * FROM salary WHERE employee_id = #{employeeId} AND month = #{month}")
    Salary findByEmployeeIdAndMonth(Long employeeId, String month);

    @Select("SELECT * FROM salary WHERE employee_id = #{employeeId} ORDER BY month DESC")
    List<Salary> findByEmployeeId(Long employeeId);
}