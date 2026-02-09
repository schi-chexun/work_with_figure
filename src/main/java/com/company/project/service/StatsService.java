package com.company.project.service;

import com.company.project.dto.StatsDTO;
import com.company.project.entity.DailyStats;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatsService {

    /**
     * 获取用户统计概览
     */
    StatsDTO getOverview(Long userId);

    /**
     * 获取指定日期的统计
     */
    DailyStats getDailyStats(Long userId, LocalDate date);

    /**
     * 获取日期范围内的统计列表
     */
    List<DailyStats> getStatsInRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 更新每日统计（内部方法，番茄钟完成时调用）
     */
    void updateDailyStats(Long userId, LocalDate date, int workSeconds, int pomodoros);

    /**
     * 计算超越百分比
     */
    Double calculateBeatPercentage(Long userId, LocalDate date);

}
