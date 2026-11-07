package com.example.hrm.modules.leave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrm.modules.leave.entity.LeaveApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LeaveMapper extends BaseMapper<LeaveApplication> {

    // ✅ 分页查询请假记录（关联员工姓名）
    @Select("SELECT l.*, u.nickname as employee_name " +
            "FROM leave_application l " +
            "LEFT JOIN employee e ON l.employee_id = e.id " +
            "LEFT JOIN sys_user u ON e.user_id = u.id " +
            "WHERE l.deleted = 0 " +
            "ORDER BY l.create_time DESC")
    Page<LeaveApplication> selectPageWithUser(Page<LeaveApplication> page);
}