package com.example.hrm.modules.department.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Department {
    private Long id;
    private Long parentId;
    private String name;
    private String leader;
    private String phone;
    private Integer sort;
    private Integer status;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}