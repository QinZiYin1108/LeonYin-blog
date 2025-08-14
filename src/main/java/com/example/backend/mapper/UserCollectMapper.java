package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.UserCollect;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏Mapper接口
 */
@Mapper
public interface UserCollectMapper extends BaseMapper<UserCollect> {
} 