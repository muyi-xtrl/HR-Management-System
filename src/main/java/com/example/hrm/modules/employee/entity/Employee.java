package com.example.hrm.modules.employee.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Employee {
    private Long id;
    private Long userId;
    private String employeeNo;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idCard;
    private Long departmentId;
    private String position;
    private LocalDate entryDate;
    private LocalDate leaveDate;
    private String status;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}