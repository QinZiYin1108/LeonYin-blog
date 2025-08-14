package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.EmailVerification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮箱验证码Mapper接口
 */
@Mapper
public interface EmailVerificationMapper extends BaseMapper<EmailVerification> {
} 