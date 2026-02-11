package com.company.project.controller;

import com.company.project.common.Result;
import com.company.project.dto.StatsDTO;
import com.company.project.entity.DailyStats;
import com.company.project.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import com.company.project.dto.KeyboardReportDTO;
import com.company.project.dto.RankItemDTO;

/**
 * 统计控制器
 */
@Tag(name = "统计数据", description = "工作时长统计相关接口")
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "上报键盘活动数据", description = "前端定时上报敲击次数和收获")
    @PostMapping("/keyboard/report")
    public Result<Void> reportKeyboard(@RequestAttribute("userId") Long userId, @RequestBody KeyboardReportDTO report) {
        statsService.reportKeyboardActivity(userId, report);
        return Result.success();
    }

    @Operation(summary = "获取今日键盘排行榜", description = "基于Redis的实时排行榜")
    @GetMapping("/keyboard/rank")
    public Result<List<RankItemDTO>> getKeyboardRank() {
        return Result.success(statsService.getDailyKeyboardRank());
    }

    @Operation(summary = "获取统计概览", description = "包含今日、本周、本月、总计数据")
    @GetMapping("/overview")
    public Result<StatsDTO> getOverview(@RequestAttribute("userId") Long userId) {
        StatsDTO stats = statsService.getOverview(userId);
        return Result.success(stats);
    }

    @Operation(summary = "获取指定日期统计")
    @GetMapping("/daily")
    public Result<DailyStats> getDailyStats(
            @RequestAttribute("userId") Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DailyStats stats = statsService.getDailyStats(userId, date);
        return Result.success(stats);
    }

    @Operation(summary = "获取日期范围内的统计")
    @GetMapping("/range")
    public Result<List<DailyStats>> getStatsInRange(
            @RequestAttribute("userId") Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<DailyStats> stats = statsService.getStatsInRange(userId, startDate, endDate);
        return Result.success(stats);
    }

    @Operation(summary = "获取超越百分比", description = "计算用户在指定日期超越了多少百分比的用户")
    @GetMapping("/beat-percentage")
    public Result<Double> getBeatPercentage(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now().minusDays(1);
        }
        Double percentage = statsService.calculateBeatPercentage(userId, date);
        return Result.success(percentage);
    }

}
