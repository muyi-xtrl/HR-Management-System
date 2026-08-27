package com.example.hrm.modules.leave.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}