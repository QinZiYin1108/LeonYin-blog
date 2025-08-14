package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.UserLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户操作日志Mapper接口
 */
@Mapper
public interface UserLogMapper extends BaseMapper<UserLog> {
} 