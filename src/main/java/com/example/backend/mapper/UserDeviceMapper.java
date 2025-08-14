package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.UserDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户设备记录Mapper接口
 */
@Mapper
public interface UserDeviceMapper extends BaseMapper<UserDevice> {
} 