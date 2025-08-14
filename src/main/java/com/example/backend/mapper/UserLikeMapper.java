package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.UserLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户点赞Mapper接口
 */
@Mapper
public interface UserLikeMapper extends BaseMapper<UserLike> {
} 