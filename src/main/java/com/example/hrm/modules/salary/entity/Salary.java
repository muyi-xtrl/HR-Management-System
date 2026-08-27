package com.example.hrm.modules.salary.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Salary {
    private Long id;
    private Long employeeId;
    private String month;
    private Double baseSalary;
    private Double bonus;
    private Double allowance;
    private Double overtimePay;
    private Double socialSecurity;
    private Double tax;
    private Double actualSalary;
    private String status;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}