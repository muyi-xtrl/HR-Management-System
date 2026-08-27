package com.example.hrm.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrm.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {  // ✅ 必须继承 BaseMapper

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);
}