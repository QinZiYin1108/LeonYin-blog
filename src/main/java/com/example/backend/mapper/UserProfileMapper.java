package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}





