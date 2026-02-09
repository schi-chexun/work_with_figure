package com.company.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.project.common.Result;
import com.company.project.dto.SettingsUpdateRequest;
import com.company.project.entity.Theme;
import com.company.project.entity.UserSettings;
import com.company.project.mapper.ThemeMapper;
import com.company.project.service.UserSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设置控制器
 */
@Tag(name = "设置管理", description = "用户设置、主题相关接口")
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final UserSettingsService userSettingsService;
    private final ThemeMapper themeMapper;

    @Operation(summary = "获取用户设置")
    @GetMapping
    public Result<UserSettings> getSettings(@RequestAttribute("userId") Long userId) {
        UserSettings settings = userSettingsService.getByUserId(userId);
        return Result.success(settings);
    }

    @Operation(summary = "更新用户设置")
    @PutMapping
    public Result<Void> updateSettings(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody SettingsUpdateRequest request) {
        userSettingsService.update(userId, request);
        return Result.success();
    }

    @Operation(summary = "获取所有主题")
    @GetMapping("/themes")
    public Result<List<Theme>> getThemes() {
        LambdaQueryWrapper<Theme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Theme::getStatus, 1)
               .orderByDesc(Theme::getIsDefault)
               .orderByAsc(Theme::getId);
        List<Theme> themes = themeMapper.selectList(wrapper);
        return Result.success(themes);
    }

    @Operation(summary = "切换主题")
    @PutMapping("/theme/{themeId}")
    public Result<Void> switchTheme(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long themeId) {
        SettingsUpdateRequest request = new SettingsUpdateRequest();
        request.setThemeId(themeId);
        userSettingsService.update(userId, request);
        return Result.success();
    }

    @Operation(summary = "切换声音特效")
    @PutMapping("/sound/{enabled}")
    public Result<Void> toggleSound(
            @RequestAttribute("userId") Long userId,
            @PathVariable Integer enabled) {
        SettingsUpdateRequest request = new SettingsUpdateRequest();
        request.setSoundEnabled(enabled);
        userSettingsService.update(userId, request);
        return Result.success();
    }

}
