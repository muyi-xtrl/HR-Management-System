package com.example.hrm.modules.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Attendance {
    private Long id;
    private Long employeeId;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
    private Double workHours;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}