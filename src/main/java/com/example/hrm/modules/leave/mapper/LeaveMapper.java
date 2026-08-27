package com.example.hrm.modules.leave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrm.modules.leave.entity.LeaveApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface LeaveMapper extends BaseMapper<LeaveApplication> {

    @Select("SELECT * FROM leave_application WHERE employee_id = #{employeeId} ORDER BY create_time DESC")
    List<LeaveApplication> findByEmployeeId(Long employeeId);

    @Select("SELECT * FROM leave_application WHERE status = '待审批' ORDER BY create_time DESC")
    List<LeaveApplication> findPendingApprovals();
}