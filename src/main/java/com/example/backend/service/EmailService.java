package com.example.backend.service;

import com.example.backend.dto.EmailVerificationResponse;

/**
 * 邮件服务接口
 */
public interface EmailService {

	/**
	 * 发送邮箱验证码
	 * @param email 收件邮箱
	 * @return 发送是否成功
	 */
	boolean sendVerificationCode(String email);

	/**
	 * 校验邮箱验证码
	 * @param email 邮箱
	 * @param code 验证码
	 * @return 是否通过
	 */
	boolean verifyCode(String email, String code);

	/**
	 * 校验验证码并生成一次性用途的邮箱验证 Token
	 * @param email 邮箱
	 * @param code 验证码
	 * @param purpose 用途标识
	 * @return 包含 token 等信息的响应
	 */
	EmailVerificationResponse verifyCodeAndGenerateToken(String email, String code, Integer purpose);

	/**
	 * 发送简单通知邮件（HTML）
	 * @param to 收件人
	 * @param subject 主题
	 * @param content 内容（支持换行）
	 */
	void sendSimpleMail(String to, String subject, String content);
}

 