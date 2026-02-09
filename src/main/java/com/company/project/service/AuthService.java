package com.company.project.service;

import com.company.project.dto.LoginRequest;
import com.company.project.dto.LoginResponse;
import com.company.project.dto.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    Long register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 刷新Token
     */
    String refreshToken(Long userId);

}
