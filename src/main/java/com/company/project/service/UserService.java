package com.company.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.User;

/**
 * 用户Service接口（示例）
 * 继承MyBatis Plus的IService获取基础CRUD方法
 *
 * @author Your Name
 * @date 2026-01-31
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return User
     */
    User getUserByUsername(String username);

}
