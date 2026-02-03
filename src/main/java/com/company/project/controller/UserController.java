package com.company.project.controller;

import com.company.project.common.Result;
import com.company.project.entity.User;
import com.company.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller（示例）
 * 接口规范整齐，只做参数校验和调用Service
 *
 * @author Your Name
 * @date 2026-01-31
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 查询用户列表
     *
     * @return Result
     */
    @Operation(summary = "查询用户列表")
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> list = userService.list();
        return Result.success(list);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return Result
     */
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     *
     * @param user 用户对象
     * @return Result
     */
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<?> save(@RequestBody User user) {
        userService.save(user);
        return Result.success("新增成功");
    }

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return Result
     */
    @Operation(summary = "更新用户")
    @PutMapping
    public Result<?> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success("更新成功");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Result
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

}
