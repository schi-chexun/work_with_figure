package com.company.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.entity.User;
import com.company.project.mapper.UserMapper;
import com.company.project.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类（示例）
 * 复杂条件查询只在impl层编写，保持接口简洁
 *
 * @author Your Name
 * @date 2026-01-31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 根据用户名查询用户
     * 复杂条件查询在impl层实现
     *
     * @param username 用户名
     * @return User
     */
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return this.getOne(queryWrapper);
    }

}
