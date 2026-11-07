package com.example.hrm.modules.leave.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("leave_application")
public class LeaveApplication {
    private Long id;
    private Long employeeId;
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double days;
    private String reason;
    private String status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String remark;

    @TableField(exist = false)  // ✅ 数据库中不存在这个字段
    private String employeeName;

    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}